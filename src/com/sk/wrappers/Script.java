package com.sk.wrappers;

import com.sk.wrappers.protocol.BasicProtocol;
import com.sk.wrappers.protocol.FieldExtractor;
import com.sk.wrappers.protocol.ProtocolGroup;
import com.sk.wrappers.protocol.extractor.ParseType;


public class Script extends ProtocolWrapper<ScriptLoader> {

	public int configId = -1;
	public int configType = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;
	
	public Script(ScriptLoader loader, int id) {
		super(loader, id, protocol );
	}

	private static final ProtocolGroup protocol = new ProtocolGroup();
	
	static {
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "configType"), new FieldExtractor(ParseType.BIG_SMART, "configId")}, 1).addSelfToGroup(protocol);
		new BasicProtocol(new FieldExtractor[]{new FieldExtractor(ParseType.UBYTE, "lowerBitIndex"), new FieldExtractor(ParseType.UBYTE, "upperBitIndex")}, 2).addSelfToGroup(protocol);
	}
}