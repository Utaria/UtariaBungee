package fr.utaria.utariabungee.util;

import fr.utaria.utariabungee.util.text.FontInfo;

public class TextUtil {

	public static String centerText(String text, int caracsNumber) {
		if (text == null || text.equals("")) return text;

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : text.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else
				if (previousCode) {
					previousCode = false;
					if (c == 'l' || c == 'L') {
						isBold = true;
						continue;
					} else isBold = false;
				} else {
					FontInfo fI = FontInfo.getFontInfo(c);
					messagePxSize += isBold ? fI.getBoldLength() : fI.getLength();
					messagePxSize++;
				}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = caracsNumber / 2 - halvedMessageSize;
		int spaceLength = FontInfo.SPACE.getLength() + 1;
		int compensated = 0;

		StringBuilder sb = new StringBuilder();

		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}

		return sb.toString() + text;
	}

}
