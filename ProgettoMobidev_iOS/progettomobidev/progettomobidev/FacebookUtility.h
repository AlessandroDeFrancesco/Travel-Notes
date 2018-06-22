//
//  FacebookUtility.h
//  Travel Notes
//
//  Created by gianpaolo on 31/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FacebookUtility : NSObject{
    NSMutableDictionary *facebookNamesMap;
    @public
    void (^_getFacebookNameCallback)(NSString *name);
}


@property (nonatomic,strong) NSMutableDictionary* facebookNamesMap;

+(FacebookUtility*) getInstance;
-(void) getNameFromID : (long long) userID andFacebookNameCallback : (void(^)(NSString*)) getFacebookCallback;
@end
