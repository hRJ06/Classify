package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Doubt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoubtDTO {
    private List<Doubt> doubtList;
}
