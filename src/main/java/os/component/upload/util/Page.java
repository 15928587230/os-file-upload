package os.component.upload.util;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Page implements Serializable {

    private static final long serialVersionUID = 7367318386929321066L;

    private long total;

    private int page;

    private int pageSize;

    public static Page getPage (long total, int page, int pageSize) {
        return new Page(total, page, pageSize);
    }
}
