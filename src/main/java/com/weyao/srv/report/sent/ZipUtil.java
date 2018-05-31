package com.weyao.srv.report.sent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP压缩工具类
 */
final class ZipUtil {

	public static UnsentFile compressToZip(UnsentFile[] files) throws IOException{
		if(files == null || files.length == 0){
			return UnsentFile.NULL;
		}
		String fileName = String.format("%s.xls", System.currentTimeMillis() + new Random().nextInt());
		File out = com.weyao.boss.common.util.FileUtil.createTempFile(fileName);
		CheckedOutputStream csum = new CheckedOutputStream(new FileOutputStream(out), new Adler32());
		//创建压缩输出流
		ZipOutputStream zos = new ZipOutputStream(csum);
		BufferedOutputStream bos = new BufferedOutputStream(zos);
		for (UnsentFile unsentFile : files) {
			BufferedReader br = new BufferedReader(new FileReader(out));
			zos.putNextEntry(new ZipEntry(unsentFile.getFileName()));
			int c;
			while((c = br.read()) != -1){
				bos.write(c);
			}
			br.close();
			bos.flush();
		}
		bos.close();
		zos.close();
		csum.close();
		return new UnsentFile(out.getAbsolutePath(), "gather.zip", files[0].getKey());
	}
}
