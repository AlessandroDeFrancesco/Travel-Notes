//
//  Element.m
//  Travel Notes
//
//  Created by gianpaolo on 06/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "Element.h"

@implementation Element

// getter e setter
@synthesize content,type,longitude,latitude,date,idElement, ownerName;

// implementazione dei costruttori
- (Element*) initWithElementType : (ElementType) newType {
    self = [super init];
    if (self){
        content = nil;
        type = newType;
        longitude = 0;
        latitude = 0;
        date = [NSDate date];
        idElement = 0;
        ownerName = @" ";
    }
    return self;
}

- (Element*) initWithElement:(Element *)element{
    self = [super init];
    if (self){
        [self setContent: element.content];
        [self setType: element.type];
        [self setLongitude: element.longitude];
        [self setLatitude: element.latitude];
        [self setDate: element.date];
        [self setIdElement: element.idElement];
        [self setOwnerName : element.ownerName];
    }
    return self;
}

@end
