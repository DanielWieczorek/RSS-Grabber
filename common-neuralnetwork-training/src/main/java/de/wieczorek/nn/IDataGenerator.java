package de.wieczorek.nn;

import java.util.List;

public interface IDataGenerator<T> {

    List<T> generate();
}
