//
//  FacebookUtility.m
//  Travel Notes
//
//  Created by gianpaolo on 31/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>

#import "FacebookUtility.h"

@implementation FacebookUtility{
    NSString *_name;
}

@synthesize facebookNamesMap;

#pragma mark - singleton method
+ (FacebookUtility*)getInstance
{
    static FacebookUtility *sharedFacebookUtility = nil;
    @synchronized(self) {
        if (sharedFacebookUtility == nil)
            sharedFacebookUtility = [[self alloc] init];
    }
    return sharedFacebookUtility;
}

- (FacebookUtility*)init{
    if (self = [super init]){
        facebookNamesMap = [NSMutableDictionary dictionary];
    }
    return self;
}


-(void) getNameFromID : (long long) userID andFacebookNameCallback : (void(^)(NSString*)) getFacebookCallback{
    _getFacebookNameCallback = [getFacebookCallback copy];
    NSString *name = [facebookNamesMap valueForKey:[NSString stringWithFormat:@"%lld",userID]];
    if (name == nil){
        if ([FBSDKAccessToken currentAccessToken]) {
            [[[FBSDKGraphRequest alloc] initWithGraphPath:[NSString stringWithFormat:@"/%lld", userID] parameters:@{@"fields": @"name"}]
             startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
                 if (!error) {
                     NSString *nameFound = result[@"name"];
                     [facebookNamesMap setValue:nameFound forKey:[NSString stringWithFormat:@"%lld",userID]];
                     _getFacebookNameCallback(nameFound);
                 }
             }];
        }
    } else {
        _getFacebookNameCallback(name);
    }
}


@end
