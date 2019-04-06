package de.bright_side.brightmarkdown;

import java.util.List;

import de.bright_side.brightmarkdown.BrightMarkdownSection.MDType;

public class BrightMarkdownFormatCascader {
	private boolean logginActive;

	public BrightMarkdownFormatCascader(boolean logginActive) {
		this.logginActive = logginActive;
	}

	protected void cascadeFormatting(BrightMarkdownSection topSection) {
		for (BrightMarkdownSection section: BrightMarkdownUtil.getAllSectionsAndSubSections(topSection, true)){
			if (section.getType() == MDType.TABLE_ROW) {
				cascadeTableRowFormatting(section);
			}
		}		
	}

	private void cascadeTableRowFormatting(BrightMarkdownSection tableRowSection) {
		if ((tableRowSection.getChildren() == null) || (tableRowSection.getChildren().isEmpty())){
			return;
		}
		
		handleCaseBackgroundAtBeginningAndEndOfRow(tableRowSection);
		for (BrightMarkdownSection cell: tableRowSection.getChildren()) {
			handleCaseBackgroundAtBeginningAndEndOfCell(cell);
		}
		
	}

	/**
	 * case beginning and end of cell
	 * given: 
	 *    - first section in a cell contains background
	 *    - the last formatting in the last cell may contain end tag
	 *    - there may be no background color end tags otherwise
	 * then:
	 *    - remove the background formatting
	 *    - assign the background formatting to the table row 
	 */
	private void handleCaseBackgroundAtBeginningAndEndOfCell(BrightMarkdownSection cellSection) {
		if (!doBackgroundAtBeginningAndEndOfCellConditionsApply(cellSection)) {
			return;
		}
		applyBackgroundFormattingToTableCell(cellSection);
		removeBackgroundColorFromBeginningOfCell(cellSection);
		removeBackgroundColorEndTagFromEndOfCellIfExisting(cellSection);
	}

	/**
	 * case background start beginning of row and end at end of row
	 * given: 
	 *    - first section in first cell contains background
	 *    - the last formatting in the last cell may contain end tag
	 *    - there may be no background color end tags otherwise
	 * then:
	 *    - remove the background formatting
	 *    - assign the background formatting to the table row 
	 * 
	 */
	private void handleCaseBackgroundAtBeginningAndEndOfRow(BrightMarkdownSection tableRowSection) {
		if (!doBackgroundAtBeginningAndEndOfRowConditionsApply(tableRowSection)) {
			return;
		}
		applyBackgroundFormattingToTableRow(tableRowSection);
		removeBackgroundColorFromBeginningOfRow(tableRowSection);
		removeBackgroundColorEndTagFromEndOfRowIfExisting(tableRowSection);
	}

	private void removeBackgroundColorEndTagFromEndOfRowIfExisting(BrightMarkdownSection tableRowSection) {
		List<BrightMarkdownSection> sections = BrightMarkdownUtil.getAllSectionsAndSubSections(tableRowSection, false);
		BrightMarkdownSection lastSection = sections.get(sections.size() - 1); 
		if (lastSection.isBackgroundColorEndTag()) {
			lastSection.setBackgroundColorEndTag(false);
		}
	}

	private void removeBackgroundColorEndTagFromEndOfCellIfExisting(BrightMarkdownSection cellSection) {
		List<BrightMarkdownSection> sections = BrightMarkdownUtil.getAllSectionsAndSubSections(cellSection, false);
		BrightMarkdownSection lastSection = sections.get(sections.size() - 1); 
		if (lastSection.isBackgroundColorEndTag()) {
			lastSection.setBackgroundColorEndTag(false);
		}
	}
	
	private void removeBackgroundColorFromBeginningOfRow(BrightMarkdownSection tableRowSection) {
		getFirstCellFormattingInTableRow(tableRowSection).setBackgroundColor(null);
	}

	private void removeBackgroundColorFromBeginningOfCell(BrightMarkdownSection cellSection) {
		getFirstCellFormattingInCell(cellSection).setBackgroundColor(null);
	}
	
	private void applyBackgroundFormattingToTableRow(BrightMarkdownSection tableRowSection) {
		String backgroundColor = getFirstCellFormattingInTableRow(tableRowSection).getBackgroundColor();
		tableRowSection.setBackgroundColor(backgroundColor);
	}
	
	private void applyBackgroundFormattingToTableCell(BrightMarkdownSection cellSection) {
		String backgroundColor = getFirstCellFormattingInCell(cellSection).getBackgroundColor();
		cellSection.setBackgroundColor(backgroundColor);
	}
	
	private BrightMarkdownSection getFirstCellFormattingInTableRow(BrightMarkdownSection tableRowSection) {
		BrightMarkdownSection firstChild = tableRowSection.getChildren().get(0);
		if ((firstChild.getChildren() == null) || (firstChild.getChildren().isEmpty())){
			return null;
		}
		return firstChild.getChildren().get(0);
	}

	private BrightMarkdownSection getFirstCellFormattingInCell(BrightMarkdownSection cellSection) {
		if ((cellSection.getChildren() == null) || (cellSection.getChildren().isEmpty())){
			return null;
		}
		return cellSection.getChildren().get(0);
	}
	
	private boolean doBackgroundAtBeginningAndEndOfRowConditionsApply(BrightMarkdownSection tableRowSection) {
		BrightMarkdownSection firstFormatting = getFirstCellFormattingInTableRow(tableRowSection);
		if (firstFormatting == null) {
			return false;
		}
		
		//: first item must have a background color
		if (firstFormatting.getBackgroundColor() == null) {
			return false;
		}
		List<BrightMarkdownSection> sections = BrightMarkdownUtil.getAllSectionsAndSubSections(tableRowSection, false);
		
		int firstCellFormattingItemIndex = 2; //: 0 is table row, 1 is the first table cell and 2 for the formatting
		
		int numberOfSections = sections.size();
		for (int i = 0; i < numberOfSections; i++) {
			BrightMarkdownSection section = sections.get(i);
			if ((section.getBackgroundColor() != null) && (i != firstCellFormattingItemIndex)){
				//: there is a background color defined in a section
				return false;
			}
			if (section.isBackgroundColorEndTag()) {
				//: background color end tag may occur only at the very end of the row
				if ((i != numberOfSections - 1) || (!BrightMarkdownUtil.isEmptyOrNull(section.getRawText()))){
					return false;
				}
			}
		}
		return true;
	}

	private boolean doBackgroundAtBeginningAndEndOfCellConditionsApply(BrightMarkdownSection cellSection) {
		BrightMarkdownSection firstFormatting = getFirstCellFormattingInCell(cellSection);
		if (firstFormatting == null) {
			return false;
		}
		
		String firstFormattingBackgroundColor = firstFormatting.getBackgroundColor(); 
		//: first item must have a background color
		if (firstFormattingBackgroundColor == null) {
			return false;
		}
		List<BrightMarkdownSection> sections = BrightMarkdownUtil.getAllSectionsAndSubSections(cellSection, false);
		
		int numberOfSections = sections.size();
		for (int i = 0; i < numberOfSections; i++) {
			BrightMarkdownSection section = sections.get(i);
			if ((section.getBackgroundColor() != null) && (i != 1) && (!section.getBackgroundColor().equals(firstFormattingBackgroundColor))){
				//: there is a background color defined in a section
				return false;
			}
			if (section.isBackgroundColorEndTag()) {
				//: background color end tag may occur only at the very end of the row
				if ((i != numberOfSections - 1) || (!BrightMarkdownUtil.isEmptyOrNull(section.getRawText()))){
					return false;
				}
			}
		}
		return true;
	}
	

}
