package de.bright_side.brightmarkdown.logic;

import java.util.List;

import de.bright_side.brightmarkdown.base.BMUtil;
import de.bright_side.brightmarkdown.model.BMSection;
import de.bright_side.brightmarkdown.model.BMSection.MDType;

public class BMFormatCascader {

	public void cascadeFormatting(BMSection topSection) {
		for (BMSection section: BMUtil.getAllSectionsAndSubSections(topSection, true)){
			if (section.getType() == MDType.TABLE_ROW) {
				cascadeTableRowFormatting(section);
			}
		}		
	}

	private void cascadeTableRowFormatting(BMSection tableRowSection) {
		if ((tableRowSection.getChildren() == null) || (tableRowSection.getChildren().isEmpty())){
			return;
		}
		
		handleCaseBackgroundAtBeginningAndEndOfRow(tableRowSection);
		for (BMSection cell: tableRowSection.getChildren()) {
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
	private void handleCaseBackgroundAtBeginningAndEndOfCell(BMSection cellSection) {
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
	private void handleCaseBackgroundAtBeginningAndEndOfRow(BMSection tableRowSection) {
		if (!doBackgroundAtBeginningAndEndOfRowConditionsApply(tableRowSection)) {
			return;
		}
		applyBackgroundFormattingToTableRow(tableRowSection);
		removeBackgroundColorFromBeginningOfRow(tableRowSection);
		removeBackgroundColorEndTagFromEndOfRowIfExisting(tableRowSection);
	}

	private void removeBackgroundColorEndTagFromEndOfRowIfExisting(BMSection tableRowSection) {
		List<BMSection> sections = BMUtil.getAllSectionsAndSubSections(tableRowSection, false);
		BMSection lastSection = sections.get(sections.size() - 1); 
		if (lastSection.isBackgroundColorEndTag()) {
			lastSection.setBackgroundColorEndTag(false);
		}
	}

	private void removeBackgroundColorEndTagFromEndOfCellIfExisting(BMSection cellSection) {
		List<BMSection> sections = BMUtil.getAllSectionsAndSubSections(cellSection, false);
		BMSection lastSection = sections.get(sections.size() - 1); 
		if (lastSection.isBackgroundColorEndTag()) {
			lastSection.setBackgroundColorEndTag(false);
		}
	}
	
	private void removeBackgroundColorFromBeginningOfRow(BMSection tableRowSection) {
		getFirstCellFormattingInTableRow(tableRowSection).setBackgroundColor(null);
	}

	private void removeBackgroundColorFromBeginningOfCell(BMSection cellSection) {
		getFirstCellFormattingInCell(cellSection).setBackgroundColor(null);
	}
	
	private void applyBackgroundFormattingToTableRow(BMSection tableRowSection) {
		String backgroundColor = getFirstCellFormattingInTableRow(tableRowSection).getBackgroundColor();
		tableRowSection.setBackgroundColor(backgroundColor);
	}
	
	private void applyBackgroundFormattingToTableCell(BMSection cellSection) {
		String backgroundColor = getFirstCellFormattingInCell(cellSection).getBackgroundColor();
		cellSection.setBackgroundColor(backgroundColor);
	}
	
	private BMSection getFirstCellFormattingInTableRow(BMSection tableRowSection) {
		BMSection firstChild = tableRowSection.getChildren().get(0);
		if ((firstChild.getChildren() == null) || (firstChild.getChildren().isEmpty())){
			return null;
		}
		return firstChild.getChildren().get(0);
	}

	private BMSection getFirstCellFormattingInCell(BMSection cellSection) {
		if ((cellSection.getChildren() == null) || (cellSection.getChildren().isEmpty())){
			return null;
		}
		return cellSection.getChildren().get(0);
	}
	
	private boolean doBackgroundAtBeginningAndEndOfRowConditionsApply(BMSection tableRowSection) {
		BMSection firstFormatting = getFirstCellFormattingInTableRow(tableRowSection);
		if (firstFormatting == null) {
			return false;
		}
		
		//: first item must have a background color
		if (firstFormatting.getBackgroundColor() == null) {
			return false;
		}
		List<BMSection> sections = BMUtil.getAllSectionsAndSubSections(tableRowSection, false);
		
		int firstCellFormattingItemIndex = 2; //: 0 is table row, 1 is the first table cell and 2 for the formatting
		
		int numberOfSections = sections.size();
		for (int i = 0; i < numberOfSections; i++) {
			BMSection section = sections.get(i);
			if ((section.getBackgroundColor() != null) && (i != firstCellFormattingItemIndex)){
				//: there is a background color defined in a section
				return false;
			}
			if (section.isBackgroundColorEndTag()) {
				//: background color end tag may occur only at the very end of the row
				if ((i != numberOfSections - 1) || (!BMUtil.isEmptyOrNull(section.getRawText()))){
					return false;
				}
			}
		}
		return true;
	}

	private boolean doBackgroundAtBeginningAndEndOfCellConditionsApply(BMSection cellSection) {
		BMSection firstFormatting = getFirstCellFormattingInCell(cellSection);
		if (firstFormatting == null) {
			return false;
		}
		
		String firstFormattingBackgroundColor = firstFormatting.getBackgroundColor(); 
		//: first item must have a background color
		if (firstFormattingBackgroundColor == null) {
			return false;
		}
		List<BMSection> sections = BMUtil.getAllSectionsAndSubSections(cellSection, false);
		
		int numberOfSections = sections.size();
		for (int i = 0; i < numberOfSections; i++) {
			BMSection section = sections.get(i);
			if ((section.getBackgroundColor() != null) && (i != 1) && (!section.getBackgroundColor().equals(firstFormattingBackgroundColor))){
				//: there is a background color defined in a section
				return false;
			}
			if (section.isBackgroundColorEndTag()) {
				//: background color end tag may occur only at the very end of the row
				if ((i != numberOfSections - 1) || (!BMUtil.isEmptyOrNull(section.getRawText()))){
					return false;
				}
			}
		}
		return true;
	}
	

}
