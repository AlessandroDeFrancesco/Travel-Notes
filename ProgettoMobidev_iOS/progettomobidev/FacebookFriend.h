//
//  FacebookFriend.h
//  Travel Notes
//
//  Created by gianpaolo on 06/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#ifndef FacebookFriend_h
#define FacebookFriend_h

#import <Foundation/Foundation.h>

@interface FacebookFriend : NSObject{
    NSString* name;
    NSString* idFriend;
    bool checked;
}

@property (nonatomic,strong) NSString* name;
@property (nonatomic,strong) NSString* idFriend;
@property (nonatomic) bool checked;

- (FacebookFriend*) initWithName: (NSString*) newName andIdFriend: (NSString*) newIdFriend;

@end

#endif /* FacebookFriend_h */
