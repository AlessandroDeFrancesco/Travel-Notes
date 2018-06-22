//
//  GeoLocationUtility.m
//  Travel Notes
//
//  Created by gianpaolo on 28/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//
#import <CoreLocation/CoreLocation.h>

#import "GeoLocationUtility.h"

@implementation GeoLocationUtility{
    NSString *_address;
}

@synthesize geoLocation;

#pragma mark - singleton method

+ (GeoLocationUtility*)getInstance
{
    static GeoLocationUtility *sharedLocation = nil;
    @synchronized(self) {
        if (sharedLocation == nil)
            sharedLocation = [[self alloc] init];
    }
    return sharedLocation;
}

- (GeoLocationUtility*)init{
    if (self = [super init]){
        geoLocation = [NSMutableDictionary dictionary];
    }
    return self;
}

-(void) setGeoLocation : (float) newLat andNewLong : (float) newLong andLocationCallback : (void(^)(NSString*)) locationCallback{
    _locationFoundCallback = [locationCallback copy];
    
    __block CLPlacemark* placemark;
    CLLocation* location = [[CLLocation alloc] initWithLatitude:newLat longitude:newLong];
    CLGeocoder* geocoder = [CLGeocoder new];
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray *placemarks, NSError *error)
     {
         if (error == nil && [placemarks count] > 0)
         {
             placemark = [placemarks lastObject];
             _address = [NSString stringWithFormat:@"%@, %@ %@", placemark.name, placemark.locality, placemark.country];
             NSLog(@"Aggiungo elmemento %@ alla location cache",_address);
             [geoLocation setObject:_address forKey:[NSString stringWithFormat:@"%f,%f",newLat,newLong]];
             _locationFoundCallback(_address);
         }
     }];
    
}

-(void) getGeoLocation : (float) newLat andNewLong : (float) newLong andLocationCallback : (void(^)(NSString*)) locationCallback{
    _locationFoundCallback = [locationCallback copy];
    if([geoLocation objectForKey:[NSString stringWithFormat:@"%f,%f",newLat,newLong]] == nil){
        NSLog(@"Location non trovata, settaggio nuovo elemento");
        [self setGeoLocation:newLat andNewLong:newLong andLocationCallback:locationCallback];
    } else{
        NSLog(@"Location trovata, recupero elemento");
        _locationFoundCallback([geoLocation objectForKey:[NSString stringWithFormat:@"%f,%f",newLat,newLong]]);
    }
}


@end
