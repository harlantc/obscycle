package edu.harvard.asc.cps.xo;


public enum ConstraintGrade {

    Easy("E"), Average("A"), Difficult("D"), Error("X");

    public String value; 
    private ConstraintGrade(String value) {
        this.value = value;
    }

    public static ConstraintGrade fromValue(String value) {
        for (ConstraintGrade x: ConstraintGrade.values())
            if (x.value.equals(value))
                return x;
        return null;
    }
}
