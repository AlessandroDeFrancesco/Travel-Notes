//
//  GeoLocationUtility.h
//  Travel Notes
//
//  Created by gianpaolo on 28/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GeoLocationUtility : NSObject{
    NSMutableDictionary *geoLocation;
    @public
    void (^_locationFoundCallback)(NSString *location);

}

@property (nonatomic,strong) NSMutableDictionary* geoLocation;

+(GeoLocationUtility*) getInstance;
-(void) setGeoLocation : (float) newLat andNewLong : (float) newLong andLocationCallback : (void(^)(NSString*)) locationCallback;
-(void) getGeoLocation : (float) newLat andNewLong : (float) newLong andLocationCallback : (void(^)(NSString*)) locationCallback;

@end
