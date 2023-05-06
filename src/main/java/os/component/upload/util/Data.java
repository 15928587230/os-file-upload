package os.component.upload.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Data implements Serializable {

    private static final long serialVersionUID = 6931645664260585711L;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Object body;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Page page;

    public static Data getData() {
        return new Data();
    }

    public static Data getData(Object data) {
        Data da = new Data();
        da.setBody(data);
        return da;
    }

    public static Data getData(Object data, Page page) {
        Data da = new Data();
        da.setBody(data);
        da.setPage(page);
        return da;
    }
}
