package com.webapp.researchhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExternalApiService {

    @Value("${serpApi.key}")
    private String serpApiKey;

    /**
     * Searches Google Scholar for papers on anything the user inputs and the provided search term.
     *
     * @param search - The search term used in the Google Scholar search.
     * @return The organic results of the search as an object in the format of a json script. Will return null if
     * the search term returns no results or the monthly amount of searches remaining is 0; Returns only the first
     * 10 search results rather than all of them on the html page after testing.
     * @throws JsonProcessingException
     * @author rg308, jwm22
     */
    public Object callApi(String search) throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();

        // check search count
        String url = "https://serpapi.com/account?api_key=" + serpApiKey;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        Map<String, Object> results = objMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        int searchesRemaining = (int) results.get("total_searches_left");
        if(searchesRemaining == 0) {
            return null;
        }

        // clean input
        search = "Reverse+Computation+" + search.trim().replaceAll("\\s+","+");

        // scrape google scholar
        url = "https://serpapi.com/search.json?engine=google_scholar&q="+ search + "&api_key=" + serpApiKey;
        restTemplate = new RestTemplate();
        response = restTemplate.getForObject(url, String.class);
        results = objMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        return results.get("organic_results");
    }
}
