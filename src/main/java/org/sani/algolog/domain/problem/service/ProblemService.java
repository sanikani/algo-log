package org.sani.algolog.domain.problem.service;

import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.problem.dto.ProblemRequest;
import org.sani.algolog.domain.problem.entity.Problem;
import org.sani.algolog.domain.problem.repository.ProblemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;

    @Transactional
    public Problem getOrCreate(ProblemRequest request) {
        return problemRepository.findByPlatformAndExternalProblemId(
                        request.getPlatform(),
                        request.getExternalProblemId()
                )
                .orElseGet(() -> saveOrFindExisting(request));
    }

    private Problem saveOrFindExisting(ProblemRequest request) {
        try {
            return problemRepository.saveAndFlush(request.toEntity());
        } catch (DataIntegrityViolationException exception) {
            return problemRepository.findByPlatformAndExternalProblemId(
                            request.getPlatform(),
                            request.getExternalProblemId()
                    )
                    .orElseThrow(() -> exception);
        }
    }
}
