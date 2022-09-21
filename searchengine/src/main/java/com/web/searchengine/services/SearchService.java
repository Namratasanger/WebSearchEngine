package com.web.searchengine.services;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.searchengine.controller.SearchController;
import com.web.searchengine.controller.SpellCheckingController;
import com.web.searchengine.references.PriorityQueue;
import com.web.searchengine.vo.DocumentVo;

@Controller
public class SearchService {
	@RequestMapping("/searcher")
	public String getQuery(@RequestParam("querystr") String query, Model model) throws IOException {
		long startTime = System.currentTimeMillis();
		try {
			SearchController searcher = new SearchController();
			PriorityQueue<Integer, String> pq = SearchController.occurrences(query);
			DocumentVo[] Docs = searcher.queue2List(pq);
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;

			setAllResults(Docs, model, query, totalTime);
		} catch (Exception e) {
			model.addAttribute("hasResult", false);
			String[] noAlt = { "None" };
			model.addAttribute("alternatives", noAlt);
		}

		return "searchResultPage";
	}

	public void setAllResults(DocumentVo[] Docs, Model model, String query, long totalTime) throws IOException {
		if (Docs.length != 0) {
			model.addAttribute("hasResult", true);
			model.addAttribute("query", query);
			model.addAttribute("resultList", Docs);
			model.addAttribute("numResult", Docs.length);
			model.addAttribute("timeUsage_res", totalTime);
		} else {
			model.addAttribute("hasResult", false);
			String[] altWords = SpellCheckingController.getAltWords(query);
			if (altWords.length != 0) {
				model.addAttribute("alternatives", altWords);
				model.addAttribute("getquery", query);
			} else {
				String[] noAlt = { "None" };
				model.addAttribute("alternatives", noAlt);
			}
		}
	}
}
