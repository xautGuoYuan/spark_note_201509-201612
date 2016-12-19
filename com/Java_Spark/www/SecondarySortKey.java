package com.Java_Spark.www;

import scala.math.Ordered;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/25.
 */
public class SecondarySortKey implements Ordered<SecondarySortKey>, Serializable {
    private  int first;
    private  int second;

    public SecondarySortKey(int first, int second) {
        this.first = first;
        this.second  = second;
    }
    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }


    @Override
    public int compare(SecondarySortKey other) {
        return 0;
    }

    @Override
    public boolean $less(SecondarySortKey other) {
        if(this.first < other.getFirst()) {
            return true;
        } else if(this.first == other.first && this.second < other.getSecond()) {
            return   true;
        }
        return false;
    }

    @Override
    public boolean $greater(SecondarySortKey other) {
        if(this.first > other.getFirst()) {
            return true;
        } else if(this.first == other.getFirst() && this.second > other.getSecond()) {
            return true;
        }
        return  false;
    }

    @Override
    public boolean $less$eq(SecondarySortKey other) {
        if(this.$less(other)) {
            return true;
        } else if(this.first == other.getFirst() && this.second == other.second) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater$eq(SecondarySortKey other) {
        if(this.$greater(other)) {
            return true;
        } else if(this.first == other.getFirst() && this.second == other.getSecond()) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(SecondarySortKey other) {
        if(this.first - other.first != 0) {
            return this.first - other.getFirst();
        } else {
            return this.second - other.getSecond();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecondarySortKey that = (SecondarySortKey) o;

        if (first != that.first) return false;
        return second == that.second;

    }

    @Override
    public int hashCode() {
        int result = first;
        result = 31 * result + second;
        return result;
    }
}

