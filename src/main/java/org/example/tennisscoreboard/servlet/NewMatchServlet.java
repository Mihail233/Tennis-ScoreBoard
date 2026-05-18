package org.example.tennisscoreboard.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tennisscoreboard.dto.MatchAndScoreResponseDTO;
import org.example.tennisscoreboard.dto.PlayersDTO;
import org.example.tennisscoreboard.dto.PlayersRequestDTO;
import org.example.tennisscoreboard.service.match.OngoingMatchesService;
import org.example.tennisscoreboard.service.player.PlayerService;
import org.example.tennisscoreboard.util.ServletUtil;
import org.example.tennisscoreboard.util.ValidationUtil;

import java.io.IOException;

@WebServlet(name = "NewMatchServlet", value = "/new-match")
public class NewMatchServlet extends HttpServlet {
    private PlayerService playerService;
    private OngoingMatchesService ongoingMatchesService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.playerService = (PlayerService) getServletContext().getAttribute("playerService");
        this.ongoingMatchesService = (OngoingMatchesService) getServletContext().getAttribute("ongoingMatchesService");

        if (playerService == null) {
            throw new IllegalStateException("playerService не найден");
        }

        if (ongoingMatchesService == null) {
            throw new IllegalStateException("ongoingMatchesService не найден");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = ServletUtil.getJson(request);

        //не переданы нужные поля -> null
        //не десериализует ненужные поля(если отправлять множество нужных и ненужных полей)
        PlayersRequestDTO playersRequestDTO = ServletUtil.deserialize(json, PlayersRequestDTO.class);
        ValidationUtil.validate(playersRequestDTO);

        PlayersDTO playersDTO = playerService.findPlayers(playersRequestDTO);
        MatchAndScoreResponseDTO matchAndScoreResponseDTO = ongoingMatchesService.addNewMatch(playersDTO);

        ServletUtil.sendResponse(HttpServletResponse.SC_CREATED, matchAndScoreResponseDTO, response);
    }
}