package com.election.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.service.mapper.data.CandidateInProgressDO;
import com.election.service.mapper.entity.Candidate;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface CandidateMapper extends BaseMapper<Candidate> {

    @Select("""
            <script>
            SELECT c.candidate_id, c.name, c.election_id, c.votes
            FROM candidate c LEFT JOIN election e ON c.electionId = e.electionId
            WHERE e.state = 1
                  and c.id > #{anchor}
            LIMIT #{pageSize}
            </script>
            """)
    List<CandidateInProgressDO> getCandidateInProgressByPage(@NonNull String anchor, @NonNull Integer pageSize);


}
