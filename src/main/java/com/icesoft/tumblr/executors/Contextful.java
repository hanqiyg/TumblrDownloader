package com.icesoft.tumblr.executors;

import com.icesoft.tumblr.state.interfaces.IContext;

public interface Contextful {
	public IContext getContext();
}
