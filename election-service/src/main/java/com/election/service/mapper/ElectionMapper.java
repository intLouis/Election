package com.election.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.service.mapper.entity.Election;
import com.election.enums.ElectionState;
import com.election.service.query.ElectionQuery;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Objects;

@Mapper
public interface ElectionMapper extends BaseMapper<Election> {

    default void updateState(final ElectionQuery.UpdateElectionState updateElectionState) {
        LocalDateTime now = LocalDateTime.now();

        final var state = updateElectionState.getState();
        final var election = Election.builder()
                .id(updateElectionState.getId())
                .electionId(updateElectionState.getElectionId())
                .updateTime(now)
                .state(state);
        if (Objects.equals(state, ElectionState.START)) {
            election.startTime(now);
        } else {
            election.endTime(now);
        }
        this.updateById(election.build());
    }
}
