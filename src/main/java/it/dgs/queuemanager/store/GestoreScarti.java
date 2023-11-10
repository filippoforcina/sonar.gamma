package it.dgs.queuemanager.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import it.dgs.queuemanager.dto.StockDTO;

@Component
public class GestoreScarti {

	private static Map<String, List<StockDTO>> mappa = new HashMap<>();

	public Map<String, List<StockDTO>> getMappa() {
		return mappa;
	}

	public void registraScarti(String type, StockDTO dto) {
		if (!mappa.containsKey(type)) {
			mappa.put(type, new ArrayList<>());
		}
		List<StockDTO> lista = mappa.get(type);
		lista.add(dto);
	}

}