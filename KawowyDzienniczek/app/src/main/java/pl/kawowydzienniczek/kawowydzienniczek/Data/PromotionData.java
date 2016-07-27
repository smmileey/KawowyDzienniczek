package pl.kawowydzienniczek.kawowydzienniczek.Data;


import java.util.Date;

public class PromotionData{

        private String id;
        private String name;
        private String description;
        private String code;
        private String image;
        private String status;
        private String left_number;
        private Date start_date;
        private Date end_date;

        public PromotionData(String id, String name, String description, String code, String image, String status,
                             String left_number, Date start_date, Date end_date){
            this.id = id;
            this.name = name;
            this.description = description;
            this.code = code;
            this.image = image;
            this.status = status;
            this.left_number = left_number;
            this.start_date = start_date;
            this.end_date = end_date;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getCode() {
            return code;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

        public String getLeft_number() {
            return left_number;
        }

        public Date getStart_date() {
            return start_date;
        }

        public Date getEnd_date() {
            return end_date;
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PromotionData that = (PromotionData) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

