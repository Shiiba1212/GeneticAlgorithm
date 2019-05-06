package geneticalgorithm;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		// パラメータ
		int LIST_SIZE = 30; // 遺伝子長
		int POPULATION_SIZE = 10; // 個体数
		int GENERATION = 80; // 世代数
		double CROSS_RATE = 0.95; // 交叉発生の確率
		double MUTATE_RATE = 0.1; // 突然変異の確率
		int SELECT_SIZE = 5; // 選択する個数

		// 初期選択＆適応度計算
		int[][] Elements = new int[POPULATION_SIZE][LIST_SIZE]; // 実集団
		int[] Fitness = new int[POPULATION_SIZE]; // 各個体の適応度

		Initialization(Elements);
		calcFitness(Elements, Fitness);
		System.out.printf("第  1 世代\n");
		DispDetail(Elements, Fitness);

		for (int g = 0; g < GENERATION - 1; g++) {
			// 選択
			int[][] nextElements = new int[POPULATION_SIZE][LIST_SIZE]; // 次世代集団
			//RouletteWheelSelection(Elements, Fitness, nextElements, SELECT_SIZE);
			EliteSelection(Elements, Fitness, nextElements, SELECT_SIZE);

			// 交叉
			TwoPointsCrossOver(nextElements, SELECT_SIZE, CROSS_RATE);

			// 突然変異
			Mutation(nextElements, MUTATE_RATE);

			// 世代交代
			ArrayCopy(nextElements, Elements);

			// 適応度の計算と表示
			calcFitness(Elements, Fitness);
			System.out.printf("第 %2d 世代\n", g + 2);
			//Disp(Elements, Fitness);
			DispDetail(Elements, Fitness);

		}

	}

	/*******************
	 **  1.初期選択   **
	 *******************/
	/*
	 * 初期選択(forOneMax)
	 *   初期個体をランダムに生成する。
	 */
	public static void Initialization(int[][] elem) {
		Random rnd = new Random();
		for (int i = 0; i < elem.length; i++) {
			for (int j = 0; j < elem[i].length; j++) {
				elem[i][j] = rnd.nextInt(2);
			}
		}
	}

	/*
	 * 適応度の計算
	 *   各個体の適応度を計算する。
	 */
	public static void calcFitness(int[][] elem, int[] fitness) {
		for (int i = 0; i < elem.length; i++) {
			fitness[i] = 0;
			for (int j = 0; j < elem[i].length; j++) {
				fitness[i] += elem[i][j];
			}
		}
	}

	/*************************
	 **  2.ルーレット選択   **
	 *************************/

	/*
	 * ルーレット選択
	 *   1.適応度の和を求める。
	 *   2.ルーレット選択開始。
	 */
	public static void RouletteWheelSelection(int[][] elem, int[] fitness, int[][] nextelem, int selectsize) {
		int totalFitness = 0;

		// 適応度の和を求める。
		for (int i = 0; i < fitness.length; i++) {
			totalFitness += fitness[i];
		}

		// 現世代から任意の個数だけ選択し、次世代に選択する
		Random rnd = new Random();
		int slct = 0;
		for (int i = 0; i < elem.length; i++) {
			int arrow = (int) (rnd.nextDouble() * totalFitness);
			int rouletteSum = 0;
			for (int ri = 0; ri < elem.length; ri++) {
				rouletteSum += fitness[ri];
				if (rouletteSum > arrow) {
					for (int j = 0; j < elem[slct].length; j++)
						nextelem[slct][j] = elem[ri][j];
					break;
				}
			}
			slct++;
			if (slct == selectsize) {
				break;
			}
		}
	}

	/*
	 * エリート選択
	 *   1.適応度順にソート
	 *   2.上位からselectsize個、次世代に選択
	 */
	public static void EliteSelection(int[][] elem, int[] fitness, int[][] nextelem, int selectsize) {
		SortAsFitness(elem, fitness);
		for(int i=0; i<selectsize;i++) {
			for(int j=0; j<elem[i].length;j++) {
				nextelem[i][j] = elem[i][j];
			}
		}
	}

	/***************
	 **  3.交叉   **
	 ***************/

	/*
	 * 二点交叉
	 *   nextElem : 格納用集団
	 *   slctSize : 親(選択した個体)の数
	 *   C_RATE   : 交叉確率(0.0～1.0)
	 */
	public static void TwoPointsCrossOver(int[][] nextElem, int slctSize, double C_RATE) {
		Random rnd = new Random();

		// 選択した個体以外を交叉、またはコピーにより生成する
		for (int i = slctSize; i < nextElem.length; i++) {
			// 親を選択
			int parents1 = rnd.nextInt(slctSize);
			int parents2 = rnd.nextInt(slctSize);
			for(;;) {
				if (parents1 == parents2)
					parents2 = rnd.nextInt(slctSize);
				else
					break;
			}

			double crossrate = rnd.nextDouble();
			if (crossrate < C_RATE) {
				// crossrateに適する場合は、ランダムに交叉点を設定・交叉を実行
				// 交叉点をランダムに設定
				int intersection1 = rnd.nextInt(nextElem[i].length);
				int intersection2 = intersection1 + rnd.nextInt(nextElem[i].length - intersection1);

				for (int j = 0; j < nextElem[i].length; j++) {
					if (intersection1 <= j && j <= intersection2) {
						nextElem[i][j] = nextElem[parents1][j];
					} else {
						nextElem[i][j] = nextElem[parents2][j];
					}
				}

			} else {
				// crossrateに適さない場合はそのままコピー
				for (int j = 0; j < nextElem[i].length; j++) {
					nextElem[i][j] = nextElem[parents1][j];
				}
			}
		}
	}

	/*
	 * 一様交叉
	 *   調整中
	 */

	/*******************
	 **  4.突然変異   **
	 *******************/

	/*
	 * 突然変異
	 */
	public static void Mutation(int[][] nextElem, double M_RATE) {
		Random rnd = new Random();
		for (int i = 0; i < nextElem.length; i = i + 2) {
			double mutaterate = rnd.nextDouble();
			if (mutaterate < M_RATE) {
				int j = rnd.nextInt(nextElem[i].length);
				nextElem[i][j] = (nextElem[i][j] + 1) % 2;

			}
		}

	}

	/*****************
	 **    その他   **
	 *****************/

	/*
	 * ２次元配列のコピー
	 *   配列Bに配列Aをコピーする
	 */
	public static void ArrayCopy(int[][] A, int[][] B) {
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				B[i][j] = A[i][j];
			}
		}
	}

	/*
	 * ※使わない
	 * 集団を適応度順(降順)にソート（挿入ソート）
	 *   elem(集団)の各個体に関連付いたfitness(適応度)をもとに、
	 *   elemとfitnessを降順にソートする。
	 *
	 */
	public static void SortAsFitness(int[][] elem, int[] fitness) {
		int min_fitness;

		for (int i = 0; i < elem.length; i++) {
			min_fitness = i; // 先頭をkeyとする

			for (int j = i + 1; j < elem.length; j++) {
				if (fitness[j] > fitness[min_fitness]) {
					min_fitness = j;
				}
			}
			int temp_fitness;
			temp_fitness = fitness[i];
			fitness[i] = fitness[min_fitness];
			fitness[min_fitness] = temp_fitness;

			int[] temp_elem = elem[i].clone();
			elem[i] = elem[min_fitness];
			elem[min_fitness] = temp_elem;
		}

	}

	/*
	 * 要素・適応度の表示
	 */
	public static void Disp(int[][] elem, int[] fitness) {
		for (int i = 0; i < elem.length; i++) {
			System.out.printf("%3d 番目 (%3d): ", i + 1, fitness[i]);
			for (int j = 0; j < elem[i].length; j++) {
				System.out.print(elem[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/*
	 * 集団の詳細のみを表示
	 *   最大適応度、最小適応度、平均適応度を表示
	 */
	public static void DispDetail(int[][] elem, int[] fitness) {
		int Maxfit = fitness[0];
		int Minfit = fitness[0];
		double Avefit = fitness[0];
		for(int i=1; i<fitness.length;i++) {
			if (fitness[i] > Maxfit)
				Maxfit = fitness[i];
			else if(fitness[i] < Minfit)
				Minfit = fitness[i];
			Avefit += fitness[i];
		}

		Avefit = Avefit / fitness.length;

		System.out.printf("最大適応度 : %d\n", Maxfit);
		System.out.printf("最小適応度 : %d\n", Minfit);
		System.out.printf("平均適応度 : %3.2f\n", Avefit);
		System.out.println();

	}

}
