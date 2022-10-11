package com.cditp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class Connection {
    @Inject
    @Qualifier("MongoDb")
    private Database database;
}
