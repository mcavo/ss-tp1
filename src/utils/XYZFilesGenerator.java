package utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cellindexmethod.CellIndexMethod;
import model.Particle;

public class XYZFilesGenerator {

	private final static String RED = "1 0 0";
	private final static String GREEN = "0 1 0";
	private final static String BLUE = "0 0 1";
	private final static String SOLID = "0";
	private final static String SEMI_TRANSPARENT = "0.8";

	public static void showNeighbours(String outputPath, CellIndexMethod cim) {
		List<String> header = getNeighboursHeader(cim.getNeighbours().size());
		List<String> lines = new ArrayList<>(header);
		addBasicBody(cim, lines);
		writeFile(outputPath + "/basic.xyz", lines);

//		for (Map.Entry<Particle, Set<Particle>> entry : cim.getNeighbours().entrySet()) {
//			lines.clear();
//			lines.addAll(header);
//
//			addShowNeighboursBody(entry, cim, lines);
//
//			writeFile(outputPath + "/id_" + entry.getKey().getId() + ".xyz", lines);
//		}
	}

	private static void addShowNeighboursBody(Entry<Particle, Set<Particle>> p, CellIndexMethod cim,
			List<String> lines) {
		lines.add(getParticleLine(p.getKey(), BLUE));
		lines.add(getParticleRcLine(p.getKey(), cim.getRc(), BLUE));
		
		for (Particle neigh : p.getValue()) {
			lines.add(getParticleLine(neigh, RED));
		}
		
		for (Map.Entry<Particle, Set<Particle>> entry : cim.getNeighbours().entrySet()) {
			if (!entry.getKey().equals(p.getKey()) && !p.getValue().contains(entry.getKey())) {
				lines.add(getParticleLine(entry.getKey(), GREEN));
			}
		}

	}

	private static List<String> getNeighboursHeader(int size) {
		List<String> header = new ArrayList<>();
		header.add(Integer.toString(size));
		header.add("ParticleId xCoordinate yCoordinate Radius R G B Transparency");
		return header;
	}

	private static void addBasicBody(CellIndexMethod cim, List<String> lines) {
		for (Map.Entry<Particle, Set<Particle>> entry : cim.getNeighbours().entrySet()) {
			lines.add(getParticleLine(entry.getKey(), GREEN));
		}
	}

	private static String getParticleLine(Particle p, String color) {
		return p.getId() + " " + p.getPoint().x + " " + p.getPoint().y + " " + p.getRadius() + " " + color + " "
				+ SOLID;
	}

	private static String getParticleRcLine(Particle p, double rc, String color) {
		return p.getId() + " " + p.getPoint().x + " " + p.getPoint().y + " " + (p.getRadius() + rc) + " " + color + " "
				+ SEMI_TRANSPARENT;
	}

	private static void writeFile(String path, List<String> lines) {
		Path file = Paths.get(path);
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
