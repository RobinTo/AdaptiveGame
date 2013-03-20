package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class ParameterHandler
{
	List<Parameter> parameters = new ArrayList<Parameter>();
	
	public void addParameter(Parameter newParameter)
	{
		parameters.add(newParameter);
	}
	
	public void saveParameters(FileHandle handle)
	{
		handle.writeString("Start of file", false);
		for (Parameter parameter : parameters)
		{
			handle.writeString(parameter.getString(), true);
			handle.writeString("New parameter", true);
		}
		handle.writeString("End of file", true);

	}
}
