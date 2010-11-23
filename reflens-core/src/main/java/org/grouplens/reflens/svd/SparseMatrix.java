/*
 * RefLens, a reference implementation of recommender algorithms.
 * Copyright 2010 Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */

package org.grouplens.reflens.svd;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMaps;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 */
public class SparseMatrix {
	private List<Int2DoubleMap> data;
	
	public SparseMatrix() {
		data = new ObjectArrayList<Int2DoubleMap>();
	}
	
	public SparseMatrix (int nrows) {
		data = new ObjectArrayList<Int2DoubleMap>(nrows);
		for (int i = 0; i < nrows; i++) {
			Int2DoubleMap m = new Int2DoubleOpenHashMap();
			m.defaultReturnValue(Double.NaN);
			data.add(m);
		}
	}
	
	public void set(int row, int col, double value) {
		while (row >= data.size()) {
			Int2DoubleMap m = new Int2DoubleOpenHashMap();
			m.defaultReturnValue(Double.NaN);
			data.add(m);
		}
		data.get(row).put(col, value);
	}
	
	public double get(int row, int col) {
		if (row >= data.size()) {
			return Double.NaN;
		} else {
			return data.get(row).get(col);
		}
	}
	
	public Int2DoubleMap row(int row) {
		if (row >= data.size()) {
			return Int2DoubleMaps.EMPTY_MAP;
		} else {
			return Int2DoubleMaps.unmodifiable(data.get(row));
		}
	}
}