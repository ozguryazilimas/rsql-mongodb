package io.rocketbase.commons.rsql.argconverters;

import io.rocketbase.commons.rsql.structs.ConversionInfo;
import io.rocketbase.commons.rsql.structs.Lazy;

public interface StringToQueryValueConverter {

    Lazy<Object> convert(ConversionInfo info);

}
