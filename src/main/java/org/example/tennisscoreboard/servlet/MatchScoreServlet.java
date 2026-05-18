package org.example.tennisscoreboard.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PointWinnerRequestDTO;
import org.example.tennisscoreboard.service.match.FinishedMatchesPersistenceService;
import org.example.tennisscoreboard.service.match.MatchScoreCalculationService;
import org.example.tennisscoreboard.service.match.OngoingMatchesService;
import org.example.tennisscoreboard.util.ServletUtil;
import org.example.tennisscoreboard.util.ValidationUtil;

import java.io.IOException;

@WebServlet(name = "MatchScoreServlet", value = "/match-score")
public class MatchScoreServlet extends HttpServlet {
    private OngoingMatchesService ongoingMatchesService;
    private MatchScoreCalculationService matchScoreCalculationService;
    private FinishedMatchesPersistenceService finishedMatchesPersistenceService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = (OngoingMatchesService) getServletContext().getAttribute("ongoingMatchesService");
        this.matchScoreCalculationService = (MatchScoreCalculationService) getServletContext().getAttribute("matchScoreCalculationService");
        this.finishedMatchesPersistenceService = (FinishedMatchesPersistenceService) getServletContext().getAttribute("finishedMatchesPersistenceService");

        if (ongoingMatchesService == null) {
            throw new IllegalStateException("ongoingMatchesService not found");
        }

        if (matchScoreCalculationService == null) {
            throw new IllegalStateException("matchScoreCalculationService not found");
        }

        if (finishedMatchesPersistenceService == null) {
            throw new IllegalStateException("finishedMatchesPersistenceService not found");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uuid = request.getParameter("uuid");
        ValidationUtil.validateUuid(uuid);
        MatchAndScoreResponseDTO matchAndScoreResponseDTO = ongoingMatchesService.getOngoingMatch(uuid);

        String json = ServletUtil.getJson(request);
        PointWinnerRequestDTO pointWinnerRequestDTO = ServletUtil.deserialize(json, PointWinnerRequestDTO.class);
        ValidationUtil.validate(pointWinnerRequestDTO, matchAndScoreResponseDTO);

        MatchAndScoreResponseDTO updatedMatchScoreResponseDTO = matchScoreCalculationService.updateMatchScore(pointWinnerRequestDTO, matchAndScoreResponseDTO);
        ongoingMatchesService.saveScore(uuid, updatedMatchScoreResponseDTO);

        ongoingMatchesService.deleteFinishedMatch(uuid, updatedMatchScoreResponseDTO.match().winner());
        finishedMatchesPersistenceService.addFinishedMatch(updatedMatchScoreResponseDTO.match());

        ServletUtil.sendResponse(HttpServletResponse.SC_CREATED, updatedMatchScoreResponseDTO, response);
    }
}
