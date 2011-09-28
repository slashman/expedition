package net.slashie.expedition;

import java.util.Random;

public class Scrambler {
	public static void main(String[] args) {
		String message = "";
		String[] words = message.split(" ");
		String newStr = "";
		for (int i = words.length-1; i >= 0; i--){
			String word = scramble(words[i]) ;
			newStr += word +" ";
		}
		System.out.println(newStr);
	}

	public static String scramble(String word) {
		String newword = "";
		int rndnum;
		Random randGen = new Random();
		boolean letter[] = new boolean[word.length()];
		do {
			rndnum = randGen.nextInt(word.length());
			if (letter[rndnum] == false) {
				newword = newword + word.charAt(rndnum);
				letter[rndnum] = true;
			}
		} while (newword.length() < word.length());
		return newword;
	}
}
