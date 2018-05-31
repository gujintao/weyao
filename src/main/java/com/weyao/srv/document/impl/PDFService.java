package com.weyao.srv.document.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.weyao.srv.document.Adapters;
import com.weyao.srv.document.DocumentService;
import com.weyao.srv.document.Template;

/**
 * 用来实现PDF操作的实现类
 * @author dujingjing
 *
 * @param <T>
 */
public class PDFService<T> implements DocumentService<T>{

	Document document = new Document();// 建立一个Document对象    
	
//	private static Font headfont ;// 设置字体大小    
    private static Font keyfont;// 设置字体大小    
    private static Font textfont;// 设置字体大小    
        
    private List<Template<T>.Col> cols = null;
    
    private String reportName;
	
    private PdfPTable table;
    
    private File file;
    
	/*
	 * 记录行号
	 */
	private int lineNum;

    private int maxWidth = Float.valueOf(PageSize.A2.getWidth()).intValue() - 50;
    
    static{    
        BaseFont bfChinese;    
        try {    
            //bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);    
        	bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//            headfont = new Font(bfChinese, 10, Font.BOLD);// 设置字体大小    
            keyfont = new Font(bfChinese, 8, Font.BOLD);// 设置字体大小    
            textfont = new Font(bfChinese, 8, Font.NORMAL);// 设置字体大小    
        } catch (Exception e) {             
            e.printStackTrace();    
        }     
    }    
	
    public PDFService() {
    	String fileName = String.format("%s.pdf", System.currentTimeMillis() + new Random().nextInt());
		file = com.weyao.boss.common.util.FileUtil.createTempFile(fileName); //创建CSV文件
//    	file = new File("D:\\" + fileName);
    	document.setPageSize(PageSize.A2);// 设置页面大小    
        try {    
        	PdfWriter.getInstance(document,new FileOutputStream(file));
        } catch (Exception e) {    
            e.printStackTrace();    
        }     
    }
    
	@Override
	public boolean createDocument(Template<T> template) {
		//打开一个PDF文档，进行预编辑
        document.open();
        //获取列定义
        cols = template.exportCols();
        for (Template<T>.Col col : cols) {
			col.clear();
		}
        //获取报表名称
        reportName = template.getReportName();
        //往PDF文档中加入一个表格
        table = new PdfPTable(cols.size());    
        try{    
            table.setTotalWidth(maxWidth);    
            table.setLockedWidth(true);    
            table.setHorizontalAlignment(Element.ALIGN_CENTER);         
            table.getDefaultCell().setBorder(1);    
        }catch(Exception e){    
            e.printStackTrace();    
        }    
        //绘制表格Title
//		table.addCell(createTitle());
		//绘制表头
		for (int i = 0; i < cols.size(); i++) {
			Template<T>.Col col = cols.get(i);
			int next = -1;
			if(i == cols.size() - 1){
				next = cols.size();
			}else{
				inner:
				for (int j = i + 1; j < cols.size(); j++) {
					if(col.getColName().equals(cols.get(j).getColName())){
						continue;
					}else{
						next = j;
						break inner;
					}
				}
			}
			//创建表头
			table.addCell(createHeader(col.getColName(), next - i));
			i = next - 1;
		}
		return true;
	}

	@Override
	public File fillBlank(List<T> source) throws Exception {
//		List<T> source = list.subList(0, 1);
		List<MergedCell> mergedCells = new ArrayList<MergedCell>();
		Method method = null;
		try{
			for (T t : source) {
				//填充数据
				String lastLeftContent = null;
				String content = null;
				for (int i = 0; i < cols.size(); i++) {
					Template<T>.Col col = cols.get(i);
					if(t instanceof Map){
						method = t.getClass().getMethod("get", Object.class);
						content = Adapters.toString(method.invoke(t, col.getKey()), col.getType());
					}else{
						method = col.getMethod();
						content = Adapters.toString(method.invoke(t), col.getType());
					}
					if(i == 0){
						lastLeftContent = content;
					}
					//说明这一列允许相同的值的Y轴相邻单元格进行合并
					if(col.isMergeAllowed()){
						if(col.getLastContent() == null){
							if(lastLeftContent != null && lastLeftContent.equals(content.trim()) && i < 4 && i > 0){
								//相同的值
								MergedCell unMerged = mergedCells.get(mergedCells.size() - 1);
								unMerged.goRight();
							}else{
								//说明是第一个单元格，那么进行单元格记录
								mergedCells.add(new MergedCell(i, lineNum, lineNum, i, i,  content));
								lastLeftContent = content;
							}
							col.logContent(content);
						}else{
							boolean goRight = lastLeftContent != null && lastLeftContent.equals(content.trim()) && i < 4 && i > 0;
							boolean goDown = col.getLastContent().equals(content.trim());
							if(goRight || goDown){
								if(goDown){
									//相同的值
									MergedCell unMerged = findMergedCell(mergedCells, i, lineNum - 1, false);
									if(unMerged != null){
										unMerged.goDown();
									}
								}
								if(goRight){
									//相同的值
									MergedCell unMerged = mergedCells.get(mergedCells.size() - 1);
									unMerged.goRight();
									lastLeftContent = content;
								}
							}else{
								//不相同，则写入原先的值，并将该记录清除
//								MergedCell unMerged = findMergedCell(mergedCells, i, lineNum - 1, true);
//								if(unMerged.xEnd > unMerged.xStart){
//									PdfPCell cell = createCell(content, textfont, Element.ALIGN_MIDDLE, 1, unMerged.xEnd - unMerged.xStart + 1, false);
//									table.addCell(cell);
//									sheet.mergeCells(unMerged.x, unMerged.xStart, unMerged.x, unMerged.xEnd);
//								}
								//填入值
//								cell = new Label(unMerged.x, unMerged.xStart, unMerged.content, CENTER_ALIGN_FORMAT);
//								sheet.addCell(cell);
								//记录新的节点
								mergedCells.add(new MergedCell(i, lineNum, lineNum, i, i, content));
								lastLeftContent = content;
							}
							col.logContent(content);
						}
					}else{
						mergedCells.add(new MergedCell(i, lineNum, lineNum, i, i, content));
//						PdfPCell cell = createCell(content, textfont, Element.ALIGN_MIDDLE, 1, 1, false);
//						table.addCell(cell);
					}
				}
				lineNum ++;
			}
			if(!mergedCells.isEmpty()){
				for (MergedCell mergedCell : mergedCells) {
					mergedCell.finish();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			document.add(table);
			document.close();
		}
		return file;
	}

	/**
	 * 绘制表格Title
	 * @return
	 */
//	private PdfPCell createTitle(){
//		return createCell(reportName, headfont, Element.ALIGN_LEFT, cols.size(), 1, true);
//	}
	
	private PdfPCell createHeader(String content, int colspan){
		return createCell(content, keyfont, Element.ALIGN_MIDDLE, colspan, 1, false);
	}
	
	/**
	 * 绘制单元格的方法
	 * @param value 单元格中的值
	 * @param font 字体设置
	 * @param align 布局
	 * @param colspan 设置横向占据
	 * @param rowspan 设置纵向占据
	 * @param boderFlag 根据该布尔值决定是否要绘制border
	 * @return 
	 */
	private PdfPCell createCell(String value, Font font, int align, int colspan, int rowspan, boolean noborder){    
        PdfPCell cell = new PdfPCell();    
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);    
        cell.setHorizontalAlignment(align);        
        cell.setColspan(colspan);  
        cell.setRowspan(rowspan);
        cell.setPhrase(new Phrase(value,font));    
        cell.setPadding(3.0f);    
        if(noborder){    
            cell.setBorder(0);    
            cell.setPaddingTop(0f);    
            cell.setPaddingBottom(2.0f);    
        }    
       return cell;    
   }
	
	/**
	 * 找出上个节点
	 * @param cells
	 * @param x
	 * @param xEnd
	 * @return
	 */
	private MergedCell findMergedCell(List<MergedCell> cells, int x, int xEnd, boolean remove){
		MergedCell mergedCell = null;
		for (int i = 0; i < cells.size(); i ++) {
			mergedCell = cells.get(i);
			if(mergedCell.x == x && mergedCell.xEnd == xEnd){
				if(remove){
					cells.remove(i);
				}
				return mergedCell;
			}
		}
		return null;
	}
	
	/**
	 * 该类表示一个需要合并的单元格信息
	 * @author dujingjing
	 *
	 */
	private class MergedCell{
		
		//列号
		private int x;
		
		private int xStart;
	
		//Y轴偏移量
		private int xEnd;
		
		//X轴偏移量
		private int yStart;
		
		private int yEnd;
		
		//内容
		private String content;
		
		MergedCell(int x, int xStart, int xEnd, int yStart, int yEnd, String content){
			this.x = x;
			this.xStart = xStart;
			this.xEnd = xEnd;
			this.yStart = yStart;
			this.yEnd = yEnd;
			this.content = content;
		}
		
		void goDown(){
			this.xEnd ++;
		}
		
		void goRight(){
			this.yEnd ++;
		}
		
		void finish(){
			table.addCell(createCell(content, textfont, Element.ALIGN_MIDDLE, yEnd - yStart + 1, xEnd - xStart + 1, false));
		}
	}

	@Override
	public String getReportName() {
		return this.reportName.split(".")[0] + ".pdf";
	}
}
