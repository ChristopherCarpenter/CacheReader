package com.sk.dist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sk.wrappers.Wrapper;
import com.sk.wrappers.WrapperLoader;

public abstract class Packer<T extends Packed> {

	protected final WrapperLoader loader;
	protected final Class<? extends Wrapper<?>> source;
	protected final Class<T> storage;
	protected final int endId;

	public <E extends WrapperLoader> Packer(E loader, Class<? extends Wrapper<E>> source, Class<T> storage) {
		this(loader, source, storage, -1);
	}

	public <E extends WrapperLoader> Packer(E loader, Class<? extends Wrapper<E>> source, Class<T> storage,
			int endId) {
		this.loader = loader;
		this.source = source;
		this.storage = storage;
		this.endId = endId;
	}

	public void pack(OutputStream output) throws IOException {
		System.out.println("Packing " + source.getName());
		ByteArrayOutputStream indices = new ByteArrayOutputStream();
		int index = 0;
		for (int id = 0; endId == -1 || id < endId; ++id) {
			if (id != 0 && id % 10 == 0)
				System.out.println();
			System.out.print(id + " ");
			Wrapper<?> wrap = getWrapper(id);
			if (wrap == null && endId == -1)
				break;
			writeIndex(indices, wrap == null ? -1 : index);
			int count = wrap == null ? 0 : pack(wrap, output);
			index += count;
		}
		System.out.println();
		System.out.println("Finished packing");
		writeIndex(indices, index);
		output.write(indices.toByteArray());
		writeIndex(output, indices.size());
	}

	protected int writeIndex(OutputStream out, int id) throws IOException {
		for (int i = 0, shift = 24; i < 4; ++i, shift -= 8) {
			int toWrite = (id >>> shift) & 0xff;
			out.write(toWrite);
		}
		return 4;
	}

	protected int writeValue(OutputStream out, int value) throws IOException {
		if (value == -1) {
			out.write(0xff);
			return 1;
		} else if (value <= 0x3f) {
			out.write(value);
			return 1;
		}
		int size = 1;
		int type = 0;
		for (; (value >>> (size * 8 - 2)) != 0; size *= 2, ++type) {
			int shift = size * 8 - 2;
			int shifted = value >>> shift;
			if (shifted == 0)
				break;
		}
		int keyByte = (value >>> (size - 1) * 8) & 0x3f | type << 6;
		out.write(keyByte);
		for (int i = 1, shift = (size - 2) * 8; i < size; ++i, shift -= 8) {
			out.write(value >>> shift);
		}
		return size;
	}

	protected int writeString(OutputStream out, String s) throws IOException {
		if (s == null)
			s = "";
		byte[] bytes = s.getBytes("UTF-8");
		out.write(bytes);
		out.write(0);
		return bytes.length + 1;
	}

	private Wrapper<?> getWrapper(int id) {
		try {
			return loader.load(id);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public boolean checkWrapper(Wrapper<?> input) {
		return source.isInstance(input);
	}

	public abstract int pack(Wrapper<?> input, OutputStream output) throws IOException;

}
