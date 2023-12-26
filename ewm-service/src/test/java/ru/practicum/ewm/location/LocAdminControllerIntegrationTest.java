package ru.practicum.ewm.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.exception.ApiError;
import ru.practicum.ewm.exception.NotSaveException;
import ru.practicum.ewm.location.controller.LocController;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.service.LocService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocController.class)
@AutoConfigureMockMvc
class LocAdminControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LocService locService;

    private final LocationDto locationDto = new LocationDto();

    @BeforeEach
    public void addLocations() {
        locationDto.setId(1L);
        locationDto.setLat(23.45f);
        locationDto.setLon(89.12f);
        locationDto.setRadius(15f);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена локация, когда локация валидна, " +
            "то ответ статус ок, и она сохраняется")
    void saveLocation_whenLocationValid_thenSavedLocation() {
        when(locService.saveLocation(any(LocationDto.class))).thenReturn(locationDto);

        String result = mockMvc.perform(post("/admin/locations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(locationDto), equalTo(result));
        verify(locService, times(1)).saveLocation(any(LocationDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена локация, когда локация валидна, " +
            "то ответ статус бед реквест, и она не сохраняется")
    void saveLocation_whenLocationNotSaves_thenReturnedBadRequest() {
        NotSaveException exception = new NotSaveException("Локация не была создана.");
        when(locService.saveLocation(any(LocationDto.class))).thenThrow(exception);

        String result = mockMvc.perform(post("/admin/locations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Локация не была создана.",
                equalTo(objectMapper.readValue(result, ApiError.class).getMessage()));
        verify(locService, times(1)).saveLocation(any(LocationDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("удалена локация, когда вызваны, то ответ статус ок")
    void deleteLocationById_whenInvoked_thenDeletedLocation() {
        long locId = 0L;
        when(locService.deleteLocationById(anyLong())).thenReturn(true);

        String result = mockMvc.perform(delete("/admin/locations/{locId}", locId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("true", equalTo(result));
        verify(locService, times(1)).deleteLocationById(anyLong());
    }

}