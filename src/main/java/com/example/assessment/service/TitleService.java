package com.example.assessment.service;

import com.example.assessment.dto.TitleDTO;
import com.example.assessment.exception.ResourceAlreadyExistException;
import com.example.assessment.exception.ResourceNotFoundException;
import com.example.assessment.model.Title;
import com.example.assessment.model.TitleId;
import com.example.assessment.repository.EmployeeRepository;
import com.example.assessment.repository.TitleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TitleService {

    private final TitleRepository titleRepository;
    private final EmployeeRepository employeeRepository;

    public List<TitleDTO> getAllTitles() {
        log.info("Fetching all titles");
        return titleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<TitleDTO> getTitlesByEmployee(Integer empNo) {
        log.info("Fetching titles for employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        return titleRepository.findByEmpNoOrderByFromDateDesc(empNo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public TitleDTO getCurrentTitle(Integer empNo) {
        log.info("Fetching current title for employee: {}", empNo);

        if (!employeeRepository.existsById(empNo)) {
            throw new ResourceNotFoundException("Employee not found with id: " + empNo);
        }

        Title title = titleRepository.findCurrentTitle(empNo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No current title found for employee: " + empNo));

        return convertToDTO(title);
    }


    public List<TitleDTO> getTitlesByName(String titleName) {
        log.info("Fetching titles with name: {}", titleName);
        return titleRepository.findByTitle(titleName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public TitleDTO createTitle(TitleDTO titleDTO) {
        log.info("Creating new title: {}", titleDTO);

        if (!employeeRepository.existsById(titleDTO.getEmpNo())) {
            throw new ResourceNotFoundException("Employee not found with id: " + titleDTO.getEmpNo());
        }

        TitleId id = new TitleId(titleDTO.getEmpNo(), titleDTO.getFromDate());
        if (titleRepository.existsById(id)) {
            throw new ResourceAlreadyExistException("Title already exists for this employee and date");
        }

        Title title = convertToEntity(titleDTO);
        Title savedTitle = titleRepository.save(title);
        log.info("Title created successfully");

        return convertToDTO(savedTitle);
    }


    public TitleDTO updateTitle(Integer empNo, String fromDate, TitleDTO titleDTO) {
        log.info("Updating title for employee: {} from date: {}", empNo, fromDate);

        TitleId id = new TitleId(empNo, java.time.LocalDate.parse(fromDate));
        Title existingTitle = titleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Title not found"));

        existingTitle.setTitle(titleDTO.getTitle());
        existingTitle.setToDate(titleDTO.getToDate());

        Title updatedTitle = titleRepository.save(existingTitle);
        log.info("Title updated successfully");

        return convertToDTO(updatedTitle);
    }


    public void deleteTitle(Integer empNo, String fromDate) {
        log.info("Deleting title for employee: {} from date: {}", empNo, fromDate);

        TitleId id = new TitleId(empNo, java.time.LocalDate.parse(fromDate));

        if (!titleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Title not found");
        }

        titleRepository.deleteById(id);
        log.info("Title deleted successfully");
    }

    private TitleDTO convertToDTO(Title title) {
        return TitleDTO.builder()
                .empNo(title.getEmpNo())
                .title(title.getTitle())
                .fromDate(title.getFromDate())
                .toDate(title.getToDate())
                .build();
    }

    private Title convertToEntity(TitleDTO dto) {
        return Title.builder()
                .empNo(dto.getEmpNo())
                .title(dto.getTitle())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .build();
    }
}
