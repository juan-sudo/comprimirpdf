package codigo.example.reducirpdf.aggregates;

public class ResponseBase {
    private int code;
    private String message;
    private Object data;

    // Constructor privado para forzar el uso del Builder
    private ResponseBase(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
    }

    // Getters
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    // Builder estático
    public static class Builder {
        private int code;
        private String message;
        private Object data;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseBase build() {
            return new ResponseBase(this);
        }
    }

    // Método estático para iniciar el builder
    public static Builder builder() {
        return new Builder();
    }
}
