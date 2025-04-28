package com.eni.winecellar.bll.impl;

import com.eni.winecellar.bll.BottleService;
import com.eni.winecellar.bo.wine.Bottle;
import com.eni.winecellar.bo.wine.Color;
import com.eni.winecellar.bo.wine.Region;
import com.eni.winecellar.repository.BottleRepository;
import com.eni.winecellar.repository.ColorRepository;
import com.eni.winecellar.repository.RegionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BottleServiceImpl implements BottleService {
	private BottleRepository bottleRepository;
	private RegionRepository regionRepository;
	private ColorRepository colorRepository;

	@Override
	public List<Bottle> loadBottles() {
		return bottleRepository.findAll();
	}

	@Override
	public Bottle loadBottleById(int bottleId) {
		if (bottleId <= 0) {
			throw new RuntimeException("Identifiant n'existe pas");
		}

		final Optional<Bottle> opt = bottleRepository.findById(bottleId);
		if (opt.isPresent()) {
			return opt.get();
		}

		throw new RuntimeException("Aucune bouteille ne correspond");
	}

	@Override
	public List<Bottle> loadBottlesByRegion(int regionId) {
		final Region region = validRegion(regionId);

		final List<Bottle> bottles = bottleRepository.findByRegion(regionId);
		if (bottles == null || bottles.isEmpty()) {
			throw new RuntimeException("Aucune bouteille ne correspond");
		}
		return bottles;
	}

	private Region validRegion(int regionId) {
		if (regionId <= 0) {
			throw new RuntimeException("Identifiant n'existe pas");
		}

		final Optional<Region> opt = regionRepository.findById(regionId);
		if (opt.isPresent()) {
			return opt.get();
		}

		throw new RuntimeException("Aucune région ne correspond");
	}

	@Override
	public List<Bottle> loadBottlesByColor(int colorId) {
		final Color color = validColor(colorId);

		final List<Bottle> bottles = bottleRepository.findByColor(colorId);
		if (bottles == null || bottles.isEmpty()) {
			throw new RuntimeException("Aucune bouteille ne correspond");
		}
		return bottles;
	}

	@Override
	public Bottle add(Bottle bottle) {
		return bottleRepository.save(bottle);
	}

	@Override
	public void delete(Integer bottleId) {
		bottleRepository.deleteById(bottleId);
	}

	private Color validColor(int colorId) {
		if (colorId <= 0) {
			throw new RuntimeException("Identifiant n'existe pas");
		}

		final Optional<Color> opt = colorRepository.findById(colorId);
		if (opt.isPresent()) {
			return opt.get();
		}

		throw new RuntimeException("Aucune couleur de vin ne correspond");
	}
}
