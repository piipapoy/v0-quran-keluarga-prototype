'use client'

import { useState } from 'react'
import { Copy, Check, UserPlus, ChevronRight, Trophy } from 'lucide-react'
import { useApp } from '@/lib/app-context'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { BottomNav } from '@/components/bottom-nav'
import { Progress } from '@/components/ui/progress'

export function FamilyScreen() {
  const { state } = useApp()
  const [copied, setCopied] = useState(false)

  const copyCode = async () => {
    await navigator.clipboard.writeText(state.familyCode)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  // Mock progress calculation
  const totalVerses = 6236
  const currentProgress = 45 + 286 // Al-Baqarah starts at verse 1
  const progressPercent = Math.round((currentProgress / totalVerses) * 100)

  return (
    <div className="min-h-screen bg-background pb-24">
      {/* Header */}
      <header className="px-5 pt-12 pb-6">
        <h1 className="text-2xl font-semibold text-foreground">{state.familyName}</h1>
        <p className="text-sm text-muted-foreground">{state.members.length} anggota</p>
      </header>

      <main className="px-5 space-y-5">
        {/* Progress Overview Card */}
        <Card className="p-5 rounded-3xl border border-border/50 overflow-hidden relative">
          {/* Decorative */}
          <div className="absolute top-0 right-0 w-24 h-24 bg-accent/20 rounded-full blur-2xl" />
          
          <div className="relative z-10">
            <div className="flex items-center gap-2 mb-4">
              <Trophy className="w-5 h-5 text-accent" />
              <span className="text-sm font-medium text-foreground">Progress Khatam</span>
            </div>

            <div className="mb-4">
              <div className="flex items-end justify-between mb-2">
                <span className="text-3xl font-semibold text-foreground">{progressPercent}%</span>
                <span className="text-sm text-muted-foreground">Target: {state.targetKhatam}</span>
              </div>
              <Progress value={progressPercent} className="h-3 rounded-full" />
            </div>

            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">
                {state.currentSurah} · Ayat {state.currentAyat}
              </span>
              <span className="text-muted-foreground">
                {currentProgress} / {totalVerses} ayat
              </span>
            </div>
          </div>
        </Card>

        {/* Invite Code Card */}
        <Card className="p-4 rounded-2xl border border-border/50">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-muted-foreground mb-1">Kode Keluarga</p>
              <p className="text-xl font-mono font-semibold text-foreground tracking-wider">
                {state.familyCode}
              </p>
            </div>
            <button
              onClick={copyCode}
              className="w-12 h-12 rounded-xl bg-secondary flex items-center justify-center transition-colors hover:bg-secondary/80"
            >
              {copied ? (
                <Check className="w-5 h-5 text-primary" />
              ) : (
                <Copy className="w-5 h-5 text-foreground" />
              )}
            </button>
          </div>
        </Card>

        {/* Invite Button */}
        <Button
          variant="outline"
          className="w-full h-14 rounded-2xl border-2 border-dashed border-primary/30 text-primary hover:bg-primary/5"
        >
          <UserPlus className="w-5 h-5 mr-2" />
          Undang Anggota Baru
        </Button>

        {/* Family Members */}
        <section>
          <h3 className="font-semibold text-foreground mb-3">Anggota Keluarga</h3>
          <Card className="rounded-2xl border border-border/50 divide-y divide-border/50">
            {state.members.map(member => (
              <MemberItem key={member.id} member={member} />
            ))}
          </Card>
        </section>

        {/* Recent Family Activity */}
        <section>
          <h3 className="font-semibold text-foreground mb-3">Riwayat Aktivitas</h3>
          <Card className="rounded-2xl border border-border/50 divide-y divide-border/50">
            {state.activities.map(activity => (
              <div key={activity.id} className="flex items-center gap-3 p-3">
                <div className="w-9 h-9 rounded-full bg-secondary flex items-center justify-center text-sm font-medium text-foreground">
                  {activity.member.charAt(0)}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-foreground">
                    <span className="font-medium">{activity.member}</span>{' '}
                    <span className="text-muted-foreground">{activity.action}</span>{' '}
                    <span className="font-medium">{activity.surah} {activity.ayat}</span>
                  </p>
                  <p className="text-xs text-muted-foreground">{activity.time}</p>
                </div>
              </div>
            ))}
          </Card>
        </section>
      </main>

      <BottomNav />
    </div>
  )
}

function MemberItem({ member }: { member: { name: string; avatar: string; lastSeen: string; lastUpdate?: string } }) {
  const isOnline = member.lastSeen === 'Online'

  return (
    <div className="flex items-center gap-3 p-4">
      <div className="relative">
        <div className="w-12 h-12 rounded-full bg-secondary flex items-center justify-center text-lg font-medium text-foreground">
          {member.avatar}
        </div>
        {isOnline && (
          <div className="absolute bottom-0 right-0 w-3.5 h-3.5 rounded-full bg-green-500 border-2 border-card" />
        )}
      </div>
      <div className="flex-1 min-w-0">
        <p className="font-medium text-foreground">{member.name}</p>
        <p className="text-xs text-muted-foreground">
          {isOnline ? 'Online' : `Terakhir aktif ${member.lastSeen}`}
        </p>
        {member.lastUpdate && (
          <p className="text-xs text-muted-foreground">
            Update: {member.lastUpdate}
          </p>
        )}
      </div>
      <ChevronRight className="w-5 h-5 text-muted-foreground" />
    </div>
  )
}
