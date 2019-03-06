package com.github.gengxiaoxiaoxin.javacv;

import org.bytedeco.javacpp.*;

/**
 * The type Image processing utils.
 *
 * @author gengxin
 */
public class ImageProcessingUtils {
	
	/**
	 * Jpg 2 png.
	 *
	 * @param inputImageFileName  the input image file name
	 * @param outputImageFileName the output image file name
	 */
	public static void jpg2png(String inputImageFileName,String outputImageFileName){
		opencv_core.Mat inputImage = opencv_imgcodecs.imread(inputImageFileName, opencv_imgcodecs.IMREAD_COLOR);
		opencv_core.Mat hsvImg = new opencv_core.Mat();
		//BGR=>HSV
		opencv_imgproc.cvtColor(inputImage, hsvImg, opencv_imgproc.CV_BGR2HSV);
		//cv::inRange()
		//cv::inRange()
		//maskBGR = cv2.inRange(bright,minBGR,maxBGR)
		//resultBGR = cv2.bitwise_and(bright, bright, mask = maskBGR)
		//https://www.learnopencv.com/color-spaces-in-opencv-cpp-python/
		int hMin = 180, hMax = 0, sMin = 255, sMax = 0, vMin = 255, vMax = 0;
		int rows = hsvImg.rows();
		int cols = hsvImg.cols();
		//calculate background color hsv
		//Horizontal
		for (int row = 0; row < rows; row++) {
			//first col
			//BytePointer is signed char,we need convert to unsigned
			BytePointer p = hsvImg.ptr(row, 0);
			//for blue
			int b = p.get(0) & 0xFF;
			if (b > hMax) {
				hMax = b;
			}
			if (b < hMin) {
				hMin = b;
			}
			// for grey
			int g = p.get(1) & 0xFF;
			if (g > sMax) {
				sMax = g;
			}
			if (g < sMin) {
				sMin = g;
			}
			// for red
			int r = p.get(2) & 0xFF;
			if (r > vMax) {
				vMax = r;
			}
			if (r < vMin) {
				vMin = r;
			}
			//last col
			p = hsvImg.ptr(row, cols - 1);
			b = p.get(0) & 0xFF;
			if (b > hMax) {
				hMax = b;
			}
			if (b < hMin) {
				hMin = b;
			}
			// for grey
			g = p.get(1) & 0xFF;
			if (g > sMax) {
				sMax = g;
			}
			if (g < sMin) {
				sMin = g;
			}
			// for red
			r = p.get(2) & 0xFF;
			if (r > vMax) {
				vMax = r;
			}
			if (r < vMin) {
				vMin = r;
			}
		}
		//Vertical
		for (int col = 0; col < cols; col++) {
			//first row
			BytePointer p = hsvImg.ptr(0, col);
			//for blue
			int b = p.get(0) & 0xFF;
			if (b > hMax) {
				hMax = b;
			}
			if (b < hMin) {
				hMin = b;
			}
			// for grey
			int g = p.get(1) & 0xFF;
			if (g > sMax) {
				sMax = g;
			}
			if (g < sMin) {
				sMin = g;
			}
			// for red
			int r = p.get(2) & 0xFF;
			if (r > vMax) {
				vMax = r;
			}
			if (r < vMin) {
				vMin = r;
			}
			//last row
			p = hsvImg.ptr(rows - 1, col);
			b = p.get(0) & 0xFF;
			if (b > hMax) {
				hMax = b;
			}
			if (b < hMin) {
				hMin = b;
			}
			// for grey
			g = p.get(1) & 0xFF;
			if (g > sMax) {
				sMax = g;
			}
			if (g < sMin) {
				sMin = g;
			}
			// for red
			r = p.get(2) & 0xFF;
			if (r > vMax) {
				vMax = r;
			}
			if (r < vMin) {
				vMin = r;
			}
		}
		
		System.out.println(opencv_core.cvScalar(hMin, sMin, vMin, 0));
		System.out.println(opencv_core.cvScalar(hMax, sMax, vMax, 0));
		
		//tolerance scope
		hMin = hMin - 1;
		hMax = hMax + 1;
		if (hMin < 0) {
			hMin = 0;
		}
		if (hMax > 255) {
			hMax = 255;
		}
		
		sMin = sMin - 50;
		sMax = sMax + 50;
		if (sMin < 0) {
			sMin = 0;
		}
		if (sMax > 255) {
			sMax = 255;
		}
		
		vMin = vMin - 50;
		vMax = vMax + 50;
		if (vMin < 0) {
			vMin = 0;
		}
		if (vMax > 255) {
			vMax = 255;
		}
		
		opencv_core.Mat maskHSV = new opencv_core.Mat();
		
		System.out.println(opencv_core.cvScalar(hMin, sMin, vMin, 0));
		System.out.println(opencv_core.cvScalar(hMax, sMax, vMax, 0));
		
		//Set the pixel value to white (255) if the pixel within two thresholds,and the pixel value in the threshold interval will be set to black (0)，
		//This feature is similar to a dual thresholding operation.
		opencv_core.inRange(hsvImg, new opencv_core.Mat(new opencv_core.Scalar(hMin, sMin, vMin, 0)), new opencv_core.Mat(new opencv_core.Scalar(hMax, sMax, vMax, 0)), maskHSV);
		opencv_imgcodecs.imwrite("D:\\mask.jpg", maskHSV);
		
		//Morphological transformation: open operation - first corrosion, then expansion. Remove small highlights from the image
		opencv_imgproc.morphologyEx(maskHSV, maskHSV, opencv_imgproc.MORPH_OPEN, new opencv_core.Mat());
		opencv_imgcodecs.imwrite("D:\\mask2.jpg", maskHSV);
		
		//GaussianBlur
		opencv_core.Mat mask = new opencv_core.Mat();
		opencv_imgproc.GaussianBlur(maskHSV, mask, new opencv_core.Size(5, 5), 0, 0, opencv_core.BORDER_DEFAULT);
		opencv_imgcodecs.imwrite("D:\\mask3.jpg", maskHSV);
		
		//Divides a multi-channel array into several single-channel arrays.
		opencv_core.MatVector srcChannels = new opencv_core.MatVector();
		opencv_core.split(inputImage, srcChannels);
		//(~mask) quivalent to (255-mask),the value inside the mask is either 0 Or 255
		srcChannels.push_back(opencv_core.not(mask).asMat());
		
		//Creates one multichannel array out of several single-channel ones.
		opencv_core.Mat dst = new opencv_core.Mat();
		opencv_core.merge(srcChannels, dst);
		//let us output a transparent image.
		opencv_imgcodecs.imwrite(outputImageFileName, dst);
	}
	
}
