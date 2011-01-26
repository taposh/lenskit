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

package org.grouplens.reflens.knn;

import java.util.Properties;

import org.grouplens.reflens.RecommenderEngineBuilder;
import org.grouplens.reflens.RecommenderModule;
import org.grouplens.reflens.data.RatingVector;
import org.grouplens.reflens.knn.params.ItemSimilarity;
import org.grouplens.reflens.knn.params.NeighborhoodSize;
import org.grouplens.reflens.knn.params.SimilarityDamper;
import org.grouplens.reflens.util.SimilarityMatrixBuilderFactory;
import org.grouplens.reflens.util.SymmetricBinaryFunction;
import org.joda.convert.StringConvert;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * TODO Extract NeighborhoodRecommenderModule
 * TODO Document this class
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 */
public class ItemRecommenderModule extends RecommenderModule {
	
	public ItemRecommenderModule() {
		super();
	}

	public ItemRecommenderModule(Properties props) {
		super(props);
	}
	
	public ItemRecommenderModule(Properties props, StringConvert converter) {
		super(props, converter);
	}

	@Override
	protected void configure() {
		super.configure();
		
		configureParameters();
		
		configureSimilarityMatrix();
		configureItemSimilarity();
		configureRecommenderBuilder();
	}
	
	protected void configureParameters() {
		configureNeighborhoodSize();
		configureSimilarityDamper();
	}

	/**
	 * 
	 */
	protected void configureRecommenderBuilder() {
		bind(RecommenderEngineBuilder.class).to(ItemItemRecommenderBuilder.class);
	}

	/**
	 * 
	 */
	protected void configureSimilarityMatrix() {
		bind(SimilarityMatrixBuilderFactory.class).toProvider(
				FactoryProvider.newFactory(SimilarityMatrixBuilderFactory.class,
						TruncatingSimilarityMatrixBuilder.class));
	}

	/**
	 * 
	 */
	protected void configureNeighborhoodSize() {
		bindProperty(int.class, NeighborhoodSize.class);
	}
	
	protected void configureSimilarityDamper() {
		bindProperty(double.class, SimilarityDamper.class);
	}
	
	protected void configureItemSimilarity() {
		bindClassParameter(new TypeLiteral<Similarity<? super RatingVector>>(){},
				ItemSimilarity.class);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Provides
	SimilarityMatrixBuildStrategy buildStrategy(
			SimilarityMatrixBuilderFactory matrixFactory,
			@ItemSimilarity Similarity<? super RatingVector> similarity) {
		if (similarity instanceof OptimizableVectorSimilarity) {
			if (similarity instanceof SymmetricBinaryFunction)
				return new OptimizedSymmetricSimilarityMatrixBuildStrategy(matrixFactory,
						(OptimizableVectorSimilarity) similarity);
			else
				return new OptimizedSimilarityMatrixBuildStrategy(matrixFactory,
						(OptimizableVectorSimilarity) similarity);
		} else {
			if (similarity instanceof SymmetricBinaryFunction)
				return new SymmetricSimilarityMatrixBuildStrategy(matrixFactory, similarity);
			else
				return new SimpleSimilarityMatrixBuildStrategy(matrixFactory, similarity);
		}
	}
}