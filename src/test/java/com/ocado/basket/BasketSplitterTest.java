package com.ocado.basket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BasketSplitterTest {
    private BasketSplitter basketSplitter;

    private List<String> readBasket(String pathToBasketJson) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(pathToBasketJson))) {
            return gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());
        }
    }

    @BeforeEach
    public void testSetup() throws IOException {
        basketSplitter = new BasketSplitter("config/config.json");
    }


    @Test
    void testSplitWithBasket1() throws IOException {
        String pathToBasket1 = "baskets/basket-1.json";
        List<String> items = readBasket(pathToBasket1);
        Map<String, List<String>> result = basketSplitter.split(items);

        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("Pick-up point"));
        assertTrue(result.containsKey("Courier"));
        assertEquals(2, result.keySet().size());
    }

    @Test
    void testSplitWithBasket2() throws IOException {
        String pathToBasket2 = "baskets/basket-2.json";
        List<String> items = readBasket(pathToBasket2);
        Map<String, List<String>> result = basketSplitter.split(items);

        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("Same day delivery"));
        assertTrue(result.containsKey("Courier"));
        assertTrue(result.containsKey("Express Collection"));
        assertEquals(3, result.keySet().size());
    }

    @Test
    void testReadConfig() {
        String pathToConfigFile = "config/config.json";
        assertDoesNotThrow(() -> basketSplitter.readConfig(pathToConfigFile));

    }

    @Test
    void setCover() {
        List<String> items = List.of("apple", "banana", "orange", "kiwi", "grapes");
        Map<String, List<String>> deliveryToProductsMap = Map.of(
                "Pick-up point", List.of("apple", "banana", "orange"),
                "Courier", List.of("grapes", "kiwi"),
                "Same day delivery", List.of("banana", "grapes"),
                "Express Collection", List.of("orange", "kiwi")
        );

        Map<String, List<String>> result = basketSplitter.performGreedySetCover(items, deliveryToProductsMap);

        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("Pick-up point"));
        assertTrue(result.containsKey("Courier"));
        assertEquals(2, result.keySet().size());
    }
}