package io.rocketbase.commons.rsql.argconverters;

import io.rocketbase.commons.rsql.structs.ConversionInfo;
import io.rocketbase.commons.rsql.structs.Lazy;

public class NoOpConverter implements StringToQueryValueConverter {

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return Lazy.fromValue(info.getArgument());
    }

}
