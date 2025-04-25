package com.eni.winecellar.bll;

import com.eni.winecellar.bo.wine.Bottle;

import java.util.List;

public interface BottleService {
	List<Bottle> loadBottles();
	Bottle loadBottleById(int bottleId);
	List<Bottle> loadBottlesByRegion(int regionId);
	List<Bottle> loadBottlesByColor(int colorId);

	Bottle add(Bottle bottle);
	void delete(Integer bottleId);
}
