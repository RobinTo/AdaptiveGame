package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.TextInputListener;

// Input lagres i String input
// Hva som skal gj�res med den, er forel�pig uvisst.
public class FeedbackTextInput implements TextInputListener {
	String input;
	List<String> numbers = new ArrayList<String>();
	
	public FeedbackTextInput()
	{
		numbers.add("0");
		numbers.add("1");
		numbers.add("2");
		numbers.add("3");
		numbers.add("4");
		numbers.add("5");
		numbers.add("6");
		numbers.add("7");
		numbers.add("8");
		numbers.add("9");
	}
	
	@Override
	public void input(String text) {
		if(numbers.contains(text))
			this.input = text;
		else
			this.input = "";
	}

	@Override
	public void canceled() {
		this.input = "";
	}
}