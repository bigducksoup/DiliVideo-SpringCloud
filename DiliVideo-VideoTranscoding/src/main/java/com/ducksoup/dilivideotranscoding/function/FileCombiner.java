package com.ducksoup.dilivideotranscoding.function;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileCombiner {
    File combine(List<File> files) throws IOException;
}
