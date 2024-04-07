package com.ocado.basket;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BasketSplitter {
    Map<String, List<String>> productToDeliveryType;

    public BasketSplitter(String absolutePathToConfigFile) throws IOException {
        this.readConfig(absolutePathToConfigFile);
    }

    public Map<String, List<String>> split(List<String> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }
        Map<String, List<String>> reducedDeliveryToProductMap = createFilteredDeliveryToProductMap(items);
        return performGreedySetCover(items, reducedDeliveryToProductMap);
    }

    protected Map<String, List<String>> createFilteredDeliveryToProductMap(List<String> items) {
        Map<String, List<String>> filteredDeliveryToProductMap = new HashMap<>();
        for(Map.Entry<String, List<String>> entry : productToDeliveryType.entrySet()){
            String product = entry.getKey();
            List<String> deliveryTypes = entry.getValue();
            if(items.contains(product)){
                for(String delivery : deliveryTypes){
                    filteredDeliveryToProductMap.computeIfAbsent(delivery, k -> new ArrayList<>()).add(product);
                }
            }

        }
        return filteredDeliveryToProductMap;
    }

    protected void readConfig(String absolutePathToConfigFile) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(absolutePathToConfigFile))) {
            productToDeliveryType = gson.fromJson(reader, Map.class);
        } catch (JsonSyntaxException ex) {
            throw new IllegalArgumentException("Invalid JSON syntax in config file", ex);
        } catch (IOException ex) {
            throw new IOException("Error reading config file", ex);
        }
    }

    // This method performs greedy approximate algorithm for set cover problem on the given items and deliveryToProductsMap
    // This greedy algorithm will always find a cover that is at most ln(n) times larger than the optimal cover
    // where n is the number of items
    protected Map<String, List<String>> performGreedySetCover(List<String> items, Map<String, List<String>> deliveryToProductsMap) {
        Set<String> itemsSet = new HashSet<>(items);
        Map<String, List<String>> output = new HashMap<>();
        while (!itemsSet.isEmpty()) {
            int maxIntersectionSize = 0;
            Optional<String> selectedDelivery = Optional.empty();
            for (Map.Entry<String, List<String>> entry : deliveryToProductsMap.entrySet()){
                int intersectionSize = (int) entry.getValue().stream().filter(itemsSet::contains).count();
                if (intersectionSize > maxIntersectionSize) {
                    maxIntersectionSize = intersectionSize;
                    selectedDelivery = Optional.of(entry.getKey());
                }
            }
            if (selectedDelivery.isEmpty()) {
                break;
            }
            List<String> itemsInSelectedDelivery = deliveryToProductsMap.get(selectedDelivery.get()).stream()
                    .filter(itemsSet::contains)
                    .toList();
            output.put(selectedDelivery.get(), itemsInSelectedDelivery);
            deliveryToProductsMap.get(selectedDelivery.get()).forEach(itemsSet::remove);
        }
        return output;
    }

}
