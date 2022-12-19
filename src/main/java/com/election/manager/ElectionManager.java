package com.election.manager;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.election.bo.ElectionBO;
import com.election.entity.Election;
import com.election.enums.ElectionState;
import com.election.mapper.ElectionMapper;
import com.election.mapstruct.ElectionConverter;
import com.election.query.ElectionQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ElectionManager {
    private final ElectionConverter electionConverter;
    private final ElectionMapper electionMapper;
    private final Snowflake snowflake;

    public void changeState(final @NonNull  ElectionBO electionBO) {
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
