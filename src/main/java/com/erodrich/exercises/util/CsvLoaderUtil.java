package com.erodrich.exercises.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.erodrich.exercises.exerciselogging.dto.ExerciseDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseLogDTO;
import com.erodrich.exercises.exerciselogging.dto.ExerciseSetDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to load CSV files
 * Format:
 * timestamp,muscle_group,exercise,weight,sets,comments,failure
 * 10/11/2025 15:49:18,Chest,Incline Dumbbell Press,30,3,"11,10,9",No
 * 10/11/2025 16:05:37,Chest,Dumbbell Flat Press,32,4,"10,9,10,8",No
 */
@Slf4j
public class CsvLoaderUtil {

	public static final String COMMA_DELIMITER = ",";
	public static final String PATH_TO_CSV = "csvfiles/userlogs.csv";

	public static void main(String[] args) throws IOException {
		List<List<String>> records = new ArrayList<>();
		ClassLoader classLoader = CsvLoaderUtil.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(PATH_TO_CSV);
		if (inputStream == null) {
			log.error("Resource not found");
			return;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(COMMA_DELIMITER);
				records.add(Arrays.asList(values));
			}
		}
		records.removeFirst();
		var logs = records.stream().map(r -> ExerciseLogDTO.builder()
				.timestamp(r.get(0))
				.exercise(ExerciseDTO.builder()
						.group(r.get(1))
						.name(r.get(2))
						.build())
				.sets(createSet(r.get(3), r.get(5)))
				.failure(Boolean.parseBoolean(r.get(6)))
				.build()).toList();

		ObjectMapper mapper = new ObjectMapper();
		log.info(mapper.writeValueAsString(logs));
	}

	private static List<ExerciseSetDTO> createSet(String weight, String reps) {
		var result = new ArrayList<ExerciseSetDTO>();
		var cleanWeight = weight.replace(",", ".").replace("\"", "");
		String[] arrayReps = reps.replace("\"", "").split(",");
		for (String arrayRep : arrayReps) {
			result.add(ExerciseSetDTO.builder()
					.weight(Double.parseDouble(cleanWeight))
					.reps(Integer.parseInt(arrayRep))
					.build());
		}
		return result;
	}
}
