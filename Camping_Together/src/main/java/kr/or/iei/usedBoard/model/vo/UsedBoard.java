package kr.or.iei.usedBoard.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsedBoard {
	private int usedBoardNo;
	private String usedBoardWriter;
	private String usedBoardTitle;
	private String usedBoardContent;
	private int usedBoardCategory;
	private int usedBoardStatus;
	private int usedProductStatus;
	private int exchangeStatus;
	private int usedDeliveryStatus;
	private String usedTradeLocation;
	private int readCount;
	private String regDate;
}