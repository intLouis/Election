package com.election.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.election.entity.ElectionResult;
import com.election.query.ElectionResultQuery;
import lombok.NonNull;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ElectionResultMapper extends BaseMapper<ElectionResult> {

    @Insert("""
            <script>
                        
                INSERT INTO
                    election_result(id,election_id,candidate_id,votes,create_time,update_time)
                VALUES
                <foreach collection="list" item="item" index="index" separator=",">
                  (
                    #{item.electionId},
                    #{item.candidateId},
                    #{item.votes},
                    #{item.createTime},
                    #{item.updateTime},
                  )
                </foreach>
                
            </script>
                   
            """)
    void batchInsert(@NonNull final List<ElectionResultQuery.BatchInsertQuery> list);


}
