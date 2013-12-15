package com.sk.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.sk.datastream.Stream;

public class LocalObjects extends StreamedWrapper<LocalObjectLoader> {

	private final List<LocalObject> objects = new ArrayList<>();

	public LocalObjects(LocalObjectLoader loader, int regionHash) {
		super(loader, regionHash);
	}

	@Override
	public void decode(Stream stream) {
		for (int id = -1, idOff = stream.getSmart(); idOff != 0; idOff = (stream.getLeft() > 0 ? stream.getSmart()
				: 0)) {
			id += idOff;
			for (int loc = 0, locOff = stream.getSmart(); locOff != 0; locOff = stream.getSmart()) {
				loc += locOff - 1;
				int plane = loc >> 12;
				int ly = loc & 0x3f;
				int lx = (loc >> 6) & 0x3f;
				int data = stream.getUByte();
				int type = data >> 2;
				int orientation = data & 0x3;
				objects.add(new LocalObject(id, lx, ly, plane, type, orientation));
			}
		}
	}

	public List<LocalObject> getObjects() {
		return objects;
	}

}
