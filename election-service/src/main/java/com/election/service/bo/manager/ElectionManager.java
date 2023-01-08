package com.election.service.bo.manager;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.election.service.bo.ElectionBO;
import com.election.service.mapper.ElectionMapper;
import com.election.service.mapper.entity.Election;
import com.election.service.mapstruct.ElectionConverter;
import com.election.service.query.ElectionQuery;
import com.election.enums.ElectionState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ElectionManager {
    private final ElectionConverter electionConverter;
    private final ElectionMapper electionMapper;
    private final Snowflake snowflake;

    public void changeState(final @NonNull ElectionBO electionBO) {
        final var updateElectionState = electionConverter.toUpdateElectionState(electionBO);
        electionMapper.updateState(updateElectionState);
    }

    public void add(@NonNull final ElectionQuery.AddElectionQuery addElectionQuery) {
        final var election = electionConverter.to(addElectionQuery);
        //默认状态
        election.setState(ElectionState.STANDBY);
        election.setElectionId(snowflake.nextIdStr());
        electionMapper.insert(election);
    }

    public ElectionBO getByElectionId(@NonNull final String electionId) {
        final var election = electionMapper.selectOne(Wrappers.<Election>lambdaQuery()
                .eq(Election::getElectionId, electionId));

        return electionConverter.to(election);
    }



}
