package com.election.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.service.mapper.entity.ElectionRecord;
import com.election.service.query.ElectionRecordQuery;
import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ElectionRecordMapper extends BaseMapper<ElectionRecord> {

    @Select("""
            <script>
            SELECT * 
            FROM election_record
            WHERE election_id = #{query.electionId}
                  <if test="query.candidateId != null and query.candidateId != ''">
                    and candidate_id = #{query.candidateId}
                  </if>
                  <if test="query.anchor != null and query.anchor != ''">
                    and id <![CDATA[ < ]]> #{query.anchor}
                  </if>
                  order by id desc 
                  limit #{query.pageSize}
            </script>
            """)
    List<ElectionRecord> getByCandidateIdAndElectionIdPage(@NonNull @Param("query") ElectionRecordQuery.GetElectionRecordByPageQuery query);


}
