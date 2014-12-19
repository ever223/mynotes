package com.xg.mynotes.db;

import com.xg.mynotes.R;

public class ChooseColor {
	
	private int mBackground_Color;
	public ChooseColor() {
	}
	public int contentBackground(int viewId) {
		switch (viewId) {
		case R.id.styleButton1:
			mBackground_Color = R.color.item_light_1;
			break;
		case R.id.styleButton2:
			mBackground_Color = R.color.item_light_2;
			break;
		case R.id.styleButton3:
			mBackground_Color = R.color.item_light_3;
			break;
		case R.id.styleButton4:
			mBackground_Color = R.color.item_light_4;
			break;
		case R.id.styleButton5:
			mBackground_Color = R.color.item_light_5;
			break;
		case R.id.styleButton6:
			mBackground_Color = R.color.item_light_6;
			break;
		case R.id.styleButton7:
			mBackground_Color = R.color.item_light_7;
			break;
		case R.id.styleButton8:
			mBackground_Color = R.color.item_light_8;
			break;
		case R.id.styleButton9:
			mBackground_Color = R.color.item_light_9;
			break;
		case R.id.styleButton10:
			mBackground_Color = R.color.item_light_10;
			break;
		case R.id.styleButton11:
			mBackground_Color = R.color.item_light_11;
			break;
		case R.id.styleButton12:
			mBackground_Color = R.color.item_light_12;
			break;
		case R.id.styleButton13:
			mBackground_Color = R.color.item_light_13;
			break;
		case R.id.styleButton14:
			mBackground_Color = R.color.item_light_14;
			break;
		case R.id.styleButton15:
			mBackground_Color = R.color.item_light_15;
			break;
		case R.id.styleButton16:
			mBackground_Color = R.color.item_light_16;
			break;
		}
		return mBackground_Color;
	}
	public int headerBackground(int contentBackground) {
		switch (contentBackground) {
		case R.color.item_light_1:
			return R.color.notes_header_1;
		case R.color.item_light_2:
			return R.color.notes_header_2;
		case R.color.item_light_3:
			return R.color.notes_header_3;
		case R.color.item_light_4:
			return R.color.notes_header_4;
		case R.color.item_light_5:
			return R.color.notes_header_5;
		case R.color.item_light_6:
			return R.color.notes_header_6;
		case R.color.item_light_7:
			return R.color.notes_header_7;
		case R.color.item_light_8:
			return R.color.notes_header_8;
		case R.color.item_light_9:
			return R.color.notes_header_9;
		case R.color.item_light_10:
			return R.color.notes_header_10;
		case R.color.item_light_11:
			return R.color.notes_header_11;
		case R.color.item_light_12:
			return R.color.notes_header_12;
		case R.color.item_light_13:
			return R.color.notes_header_13;
		case R.color.item_light_14:
			return R.color.notes_header_14;
		case R.color.item_light_15:
			return R.color.notes_header_15;
		case R.color.item_light_16:
			return R.color.notes_header_16;
		default:
			break;
		}
		return R.color.notes_header_1;
	}
	
	public int mainBackground(int contentBackground) {
		switch (contentBackground) {
		case R.color.item_light_1:
			return R.color.main_background_1;
		case R.color.item_light_2:
			return R.color.main_background_2;
		case R.color.item_light_3:
			return R.color.main_background_3;
		case R.color.item_light_4:
			return R.color.main_background_4;
		case R.color.item_light_5:
			return R.color.main_background_5;
		case R.color.item_light_6:
			return R.color.main_background_6;
		case R.color.item_light_7:
			return R.color.main_background_7;
		case R.color.item_light_8:
			return R.color.main_background_8;
		case R.color.item_light_9:
			return R.color.main_background_9;
		case R.color.item_light_10:
			return R.color.main_background_10;
		case R.color.item_light_11:
			return R.color.main_background_11;
		case R.color.item_light_12:
			return R.color.main_background_12;
		case R.color.item_light_13:
			return R.color.main_background_13;
		case R.color.item_light_14:
			return R.color.main_background_14;
		case R.color.item_light_15:
			return R.color.main_background_15;
		case R.color.item_light_16:
			return R.color.main_background_16;
		default:
			break;
		}
		return R.color.main_background_1;
	}
//	public int widgetBackground(int contentBackground) {
//		switch (contentBackground) {
//		case R.color.item_light_1:
//			return R.color.widget_small_background_1;
//		case R.color.item_light_2:
//			return R.color.widget_small_background_2;
//		case R.color.item_light_3:
//			return R.color.widget_small_background_3;
//		case R.color.item_light_4:
//			return R.color.widget_small_background_4;
//		case R.color.item_light_5:
//			return R.color.widget_small_background_5;
//		case R.color.item_light_6:
//			return R.color.widget_small_background_6;
//		case R.color.item_light_7:
//			return R.color.widget_small_background_7;
//		case R.color.item_light_8:
//			return R.color.widget_small_background_8;
//		case R.color.item_light_9:
//			return R.color.widget_small_background_9;
//		case R.color.item_light_10:
//			return R.color.widget_small_background_10;
//		case R.color.item_light_11:
//			return R.color.widget_small_background_11;
//		case R.color.item_light_12:
//			return R.color.widget_small_background_12;
//		case R.color.item_light_13:
//			return R.color.widget_small_background_13;
//		case R.color.item_light_14:
//			return R.color.widget_small_background_14;
//		case R.color.item_light_15:
//			return R.color.widget_small_background_15;
//		case R.color.item_light_16:
//			return R.color.widget_small_background_16;
//		default:
//			break;
//		}
//		return R.color.widget_small_background_1;
//	}
}
